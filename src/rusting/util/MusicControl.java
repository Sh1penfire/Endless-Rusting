package rusting.util;

import arc.Core;
import arc.Events;
import arc.assets.AssetDescriptor;
import arc.assets.loaders.MusicLoader.MusicParameter;
import arc.audio.Music;
import arc.files.Fi;
import arc.func.Boolf;
import arc.func.Boolp;
import arc.math.Mathf;
import arc.struct.*;
import arc.util.*;
import mindustry.core.GameState;
import mindustry.core.GameState.State;
import mindustry.game.EventType;
import mindustry.game.EventType.StateChangeEvent;
import mindustry.type.SectorPreset;
import rusting.game.ERSectorPreset;
import rusting.util.MusicControl.MusicSecController.MusicSecSegment;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Objects;

import static mindustry.Vars.*;
import static rusting.Varsr.music;

//note: Yoinked from BetaMindy, originally from there. Go check it out, it's a good mod.
public class MusicControl {
    public final static String musicMod = "endless-rusting";

    public static final String[] musicFiles = {"omnipresentGlare", "march"};
    public Music[] musics;
    //used to hold all the songs that can play on a tick
    public static Seq<MusicSecSegment> possibleCandidates = Seq.with();
    public static Seq<Boolp> candidatesBools = Seq.with();

    public @Nullable MusicHolder current = null;

    //current holder's importance for convenience
    protected int cimportance = 0;
    protected @Nullable Boolp cpred = null;
    protected boolean fadeinNext = true;
    public boolean waiting = false;
    boolean stillSearching = true;

    private int fadeMode = 0;
    //1: wait for current song to finish, fade vanilla and custom out, then play
    //2: interrupt current song, fade it out, then fade in next
    //3: fade current song out, nothing else
    private int lastVol = -1;

    private ERSectorPreset sector;
    public ERSectorPreset currentMusicSector;
    public MusicSecSegment secSegment;
    public Queue<MusicHolder> musicQue = new Queue<>();
    public Class<?> musicClass = Music.class;

    public static Seq<MusicHolder> pooled = new Seq(3);

    public class MusicHolder{
        public Music music;
        public float fade = 1;
        public float duration = 0, totalDuration = 0;
        public int importance = 0;
        public Boolp playUntil = () -> true;

        public void reset() {
            music = null;
            fade = 1;
            duration = 0;
            totalDuration = 0;
            importance = 0;
            playUntil = () -> true;
        }

        public MusicHolder setup(Music music, int importance, float totalDuration, boolean loop, Boolp play){
            this.music = music;
            this.importance = importance;
            this.totalDuration = totalDuration;
            music.setLooping(loop);
            playUntil = play;
            return this;
        }
    }

    public MusicHolder get(){
        return pooled.size > 0 ? pooled.get(pooled.size - 1) : new MusicHolder();
    }

    //each sector has it's own music controller, which can do things
    public static class MusicSecController{
        private static int importance;
        private static int trueImportance;
        private float musicPlayTime = 0;
        public float musicChangeDelay = 15;
        public static class MusicSecSegment{
            public MusicSecSegment(int id, boolean loop){
                musicId = id;
                this.loop = loop;
            }
            public boolean loop;
            public int musicId;
            public float playChance = 0.0015f;
            //asign a value to this field to get the music to play
            public float duration = 0;
        }

        public Seq<ObjectMap<Boolf<GameState>, Seq<MusicSecSegment>>> musicMap = Seq.with();

        public MusicSecController(){

        }

        public void update(){
            if(musicPlayTime < musicChangeDelay) {
                musicPlayTime += 1;
                return;
            }

            music.current.duration += 1;

            musicPlayTime = 0;
            music.stillSearching = true;
            importance = 0;
            possibleCandidates.clear();
            candidatesBools.clear();
            music.currentMusicSector.musicSecController.musicMap.each((ms) -> {
                importance++;
                if(!music.stillSearching || (music.current != null) && importance <= music.cimportance) return;
                Log.info("Attempting to play tracks with importance of: " + importance + " compared to " + music.cimportance);
                ms.each((b, m) -> {
                    Log.info("Searching: " + music.stillSearching);
                    if(!music.stillSearching) return;
                    music.secSegment = m.random();
                    Log.info("Able to play: " + b.get(state) + " " + music.musics[music.secSegment.musicId].toString());
                    if(b.get(state) && (music.secSegment.playChance == 1 || Mathf.chance(music.secSegment.playChance))) {
                        Log.info("Posible track found!");
                        possibleCandidates.add(music.secSegment);
                        candidatesBools.add(() -> !b.get(state));
                        trueImportance = importance;
                    }
                });
            });
            if(possibleCandidates.size != 0) {
                Log.info("Playing now: " + music.musics[music.secSegment.musicId].toString());
                music.secSegment = possibleCandidates.get(possibleCandidates.size - 1);
                control.sound.stop();

                music.stillSearching = false;
                music.playUntil(music.secSegment.musicId, candidatesBools.get(candidatesBools.size - 1), trueImportance, music.secSegment.duration * 60,true, false, music.secSegment.loop);
            }
        }
    }

    public Boolf<SectorPreset> bSector = (sectorPreset -> {
        if(sectorPreset instanceof ERSectorPreset){
            sector = (ERSectorPreset) sectorPreset;
            return true;
        }
        return false;
    });

    public Seq<ERSectorPreset> musicSectors = Seq.with();

    public void init(){
        musics = new Music[musicFiles.length];
        Events.on(EventType.FileTreeInitEvent.class, e -> {
            //load music here
            for(int i = 0; i < musicFiles.length; i++){
                musics[i] = loadMusic(musicFiles[i]);
            }

        });

        Events.on(StateChangeEvent.class, e -> {
            if(e.to == State.playing && e.from == State.menu){
                setupSector();
            }
            else if(e.to == State.menu) currentMusicSector = null;
        });
    }

    public void setupSector(){
        if(state.isCampaign() && bSector.get(state.getSector().preset) && musicSectors.contains(sector) && sector.musicChance != 0 && musicSectors.size > 0){
            currentMusicSector = sector;
        }
        else if(bSector.get(musicSectors.find(this::isValidSector))) currentMusicSector = sector;
    }

    public boolean isValidSector(ERSectorPreset erSectorPreset){
        sector = erSectorPreset;
        return state.map.file.name().equals(sector.name.substring(sector.minfo.mod.name.length() + 1) + "." + mapExtension);
    }

    public void next(){
        musicQue.removeFirst();
        current = musicQue.first();
    }

    public void update(){
        if(state == null || !state.isGame()){
            if(current != null) reset();
        }

        if(currentMusicSector != null){
            currentMusicSector.musicSecController.update();
        }
    }

    public void playUntil(int id, @Nullable Boolp end, int importance, float duration, boolean fadein, boolean wait, boolean loop){
        if(id < 0 || importance < cimportance && current != null) return; //do not use this method to shut it up!
        cpred = end;
        cimportance = Math.max(0, importance);

        if(current == null){
            fadeMode = 1;
            fadeinNext = fadein;
            current = get();
            waiting = wait;
        }
        else{
            play(musics[id], importance, duration, fadein, wait, loop, end);
        }
    }

    public void play(Music music, int importance, float duration, boolean fadein, boolean wait, boolean loop, Boolp boolp){
        fadeMode = wait ? 1 : 2;
        if(lastVol != -1){
            Core.settings.put("musicvol", lastVol);
            lastVol = -1;
        }

        if(!wait){
            if(musicQue.size > 1) next();
            current.music.play();
            current.music.setVolume(fadein ? 0f : Core.settings.getInt("musicvol") / 100f);
        }
        else{
            musicQue.addLast(get().setup(music, importance, duration, loop, boolp));
        }
    }

    //hipity hpity-
    public double calculateDuration(final File oggFile) throws IOException {
        int rate = -1;
        int length = -1;

        int size = (int) oggFile.length();
        byte[] t = new byte[size];

        FileInputStream stream = new FileInputStream(oggFile);
        stream.read(t);

        for (int i = size-1-8-2-4; i>=0 && length<0; i--) { //4 bytes for "OggS", 2 unused bytes, 8 bytes for length
            // Looking for length (value after last "OggS")
            if (
                    t[i]==(byte)'O'
                            && t[i+1]==(byte)'g'
                            && t[i+2]==(byte)'g'
                            && t[i+3]==(byte)'S'
            ) {
                byte[] byteArray = new byte[]{t[i+6],t[i+7],t[i+8],t[i+9],t[i+10],t[i+11],t[i+12],t[i+13]};
                ByteBuffer bb = ByteBuffer.wrap(byteArray);
                bb.order(ByteOrder.LITTLE_ENDIAN);
                length = bb.getInt(0);
            }
        }
        for (int i = 0; i<size-8-2-4 && rate<0; i++) {
            // Looking for rate (first value after "vorbis")
            if (
                    t[i]==(byte)'v'
                            && t[i+1]==(byte)'o'
                            && t[i+2]==(byte)'r'
                            && t[i+3]==(byte)'b'
                            && t[i+4]==(byte)'i'
                            && t[i+5]==(byte)'s'
            ) {
                byte[] byteArray = new byte[]{t[i+11],t[i+12],t[i+13],t[i+14]};
                ByteBuffer bb = ByteBuffer.wrap(byteArray);
                bb.order(ByteOrder.LITTLE_ENDIAN);
                rate = bb.getInt(0);
            }

        }
        stream.close();

        double duration = (double) (length*1000) / (double) rate;
        return duration;
    }

    public void reset(){
        if(current != null){
            current.music.setLooping(false);
            if(current.music.isPlaying()) current.music.stop();
        }
        current = null;
        cpred = null;
        fadeMode = 0;
        cimportance = 0;
        if(lastVol != -1){
            Core.settings.put("musicvol", lastVol);
            lastVol = -1;
        }
    }

    public boolean shouldEnd(){
        return (cpred != null && cpred.get());
    }

    public Music loadMusic(String soundName){
        if(headless) return new Music();

        String name = "music/" + soundName;
        String path = tree.get(name + ".ogg").exists() ? name + ".ogg" : name + ".mp3";

        Music music = new Music();
        AssetDescriptor<?> desc = Core.assets.load(path, Music.class, new MusicParameter(music));
        desc.errored = Throwable::printStackTrace;

        return music;
    }

    //I literaly have no idea what this code does
    public void rrrrr() throws IOException {
        Fi fil = tree.get("music/march.ogg");
        File file = fil.file();
        String path = file.getAbsolutePath();

        Log.info(fil.exists());
        Log.info(fil);
        Log.info(file);
        Log.info(path);
        FileInputStream fileInputStream = null;
        long duration = 0;

        try {
            fileInputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            Log.err(e);
        }

        try {
            duration = Objects.requireNonNull(fileInputStream).getChannel().size() / 128;
        } catch (IOException e) {
            Log.err(e);
        }

        Log.info(duration);
    }
}
