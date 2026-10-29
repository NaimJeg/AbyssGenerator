(function () {
    var Minecraft = Java.loadClass('net.minecraft.client.Minecraft');
    var ConnectScreen = Java.loadClass('net.minecraft.client.gui.screens.ConnectScreen');
    var TitleScreen = Java.loadClass('net.minecraft.client.gui.screens.TitleScreen');
    var Address = Java.loadClass('net.minecraft.client.multiplayer.resolver.ServerAddress');
    var ServerData = Java.loadClass('net.minecraft.client.multiplayer.ServerData');
    var Type = Java.loadClass('net.minecraft.client.multiplayer.ServerData$Type');
    var mc = Minecraft.getInstance();
    var address = typeof acceptanceAddress === 'undefined' ? '127.0.0.1:25566' : acceptanceAddress;
    ConnectScreen.startConnecting(new TitleScreen(), mc, Address.parseString(address),
        new ServerData('Abyss acceptance', address, Type.OTHER), false, null);
    return 'connection requested; poll client world before assertions';
})();
