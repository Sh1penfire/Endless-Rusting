Events.on(ClientLoadEvent,
    e => {
        let unit = Vars.content.units().find(u => u.name == "endless-rusting-guardian-sulphur-stingray");
        Log.info("hai");
        if(Version.number > 6 && !Vars.headless){
            unit.omniMovement = true;
            unit.faceTarget = false;
            unit.targetFlags = [BlockFlag.turret, null];
        }
        else unit.targetFlag = BlockFlag.turret;
    }
);