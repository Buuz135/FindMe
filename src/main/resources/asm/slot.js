function initializeCoreMod() {
    return {
        'generateChest': {
            'target': {
                'type': 'METHOD',
                'class': 'net.minecraft.client.gui.screen.inventory.ContainerScreen',
                'methodName': 'func_146977_a',
                'methodDesc': '(Lnet/minecraft/inventory/container/Slot;)V'
            },
            'transformer': function (method) {
                print("[FindMe] Patching ContainerScreen::drawSlot");

                var ASMAPI = Java.type('net.minecraftforge.coremod.api.ASMAPI');
                var Opcodes = Java.type('org.objectweb.asm.Opcodes');
                var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');
                var InsnList = Java.type('org.objectweb.asm.tree.InsnList');

                var instr = method.instructions;
                var insn = new InsnList();
                insn.add(new VarInsnNode(Opcodes.ALOAD, 0));
                insn.add(new VarInsnNode(Opcodes.ALOAD, 1));
                insn.add(ASMAPI.buildMethodCall("com/buuz135/findme/core/ScreenSlotRenderer", "drawSlot", "(Lnet/minecraft/client/gui/screen/inventory/ContainerScreen;Lnet/minecraft/inventory/container/Slot;)V", ASMAPI.MethodType.STATIC));
                instr.insert(insn);

                return method;
            }
        }
    }
}
