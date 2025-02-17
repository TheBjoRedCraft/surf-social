package dev.slne.surf.social.chat.command.channel

import dev.jorel.commandapi.CommandAPICommand

class ChannelCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission("surf.chat.command.channel")
        withSubcommand(ChannelAcceptInviteCommand("accept"))
        withSubcommand(ChannelCreateCommand("create"))
        withSubcommand(ChannelDeleteCommand("delete"))
        withSubcommand(ChannelInviteCommand("invite"))
        withSubcommand(ChannelInviteRevokeCommand("revoke"))
        withSubcommand(ChannelInfoCommand("info"))
        withSubcommand(ChannelMembersCommand("members"))
        withSubcommand(ChannelListCommand("list"))
        withSubcommand(ChannelJoinCommand("join"))
        withSubcommand(ChannelLeaveCommand("leave"))
        withSubcommand(ChannelStateToggleCommand("toggleState"))
        withSubcommand(ChannelBanCommand("ban"))
        withSubcommand(ChannelUnBanCommand("unban"))
        withSubcommand(ChannelDemoteCommand("demote"))
        withSubcommand(ChannelPromoteCommand("promote"))
        withSubcommand(ChannelKickCommand("kick"))
        withSubcommand(ChannelMoveCommand("move"))
        withSubcommand(ChannelForceDeleteCommand("forceDelete"))
        withSubcommand(ChannelForceJoinCommand("forceJoin"))
        withSubcommand(ChannelTransferOwnerShipCommand("transferOwnership"))
    }
}
