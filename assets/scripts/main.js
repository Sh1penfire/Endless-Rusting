Events.on(ClientLoadEvent.class,
    e => {
        let unit = Vars.content.units().find(u => u.name == "endless-rusting-guardian-sulphur-stingray");
        Log.info("hai");
        if(Version.number > 6 && !Vars.headless){
            unit.omniMovement = true;
            unit.targetFlags = [BlockFlag.turret, null];
        }
        else unit.targetFlag = BlockFlag.turret;
    }
);

/*
const outOfPlace = extend(Planet, "out of place", Planets.sun, 3, 3, {
    bloom: true,
    accessible: false,
    meshLoader: () => extend(SunMesh, this, 4, 5, 0.3, 1.7, 1.2, 1, 1.1,
        Color.valueOf("ff7a38"),
        Color.valueOf("ff9638"),
        Color.valueOf("ffc64c"),
        Color.valueOf("ffc6z4c"),
        Color.valueOf("ffe371"),
        Color.valueOf("f4ee8e"), 
       {}
    )
});

*/

/*

function setSand(arr){

    for(let i = 0; i < arr.length; i++){
      arr[i].item = Items.sand;
    }

}

Vars.content.blocks().each(b => {

    setSand(b.requirements);

    try{
       setSand(b.consumes.getItem().items);
       b.outputItem.item = Items.sand
    }
    catch(e){

    }

    try{
       if(b != Blocks.air) b.itemDrop = Items.sand;
    }
    catch(Idontcare){

    }
});

Vars.content.items().each(i => {
    
    if(i != Items.sand){
        i.localizedName = "[red]An inferior item, " + i.localizedName;
        i.description += "\n\nAnd this is entirely wrong.";
        if(i.details != null) i.details += "\n\nlmao look at all this shit";
    }
    else{
        i.localizedName = "[white]Our lord and saviour Sand";
    }
})
