package ml.codeboy.itemmagnet;

import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.PlayerData;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class GriefUtil {

    public static boolean isAllowed(Player player, Location location) {
        if (!GriefPrevention.instance.claimsEnabledForWorld(location.getWorld())) return true;

        PlayerData playerData = GriefPrevention.instance.dataStore.getPlayerData(player.getUniqueId());
        Claim claim = GriefPrevention.instance.dataStore.getClaimAt(location, false, playerData.lastClaim);

        //exception: administrators in ignore claims mode
        if (playerData.ignoreClaims) return true;

        //wilderness rules
        if (claim == null) {
            return true;
        } else {
            //cache the claim for later reference
            playerData.lastClaim = claim;

            //if not in the wilderness, then apply claim rules (permissions, etc)
            String cancel = claim.allowAccess(player);


            return cancel == null;
        }
    }
}
