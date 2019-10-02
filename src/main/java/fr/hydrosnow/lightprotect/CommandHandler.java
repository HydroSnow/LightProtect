package fr.hydrosnow.lightprotect;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

class CommandHandler {
	public CommandHandler() {

	}

	@SuppressWarnings("deprecation")
	public static boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
		if (cmd.getName().equalsIgnoreCase("lightprotect")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(Resources.ERROR_COLOR + "Erreur :" + Resources.PRIMARY_COLOR + " La console ne peut pas utiliser LightProtect.");
				return true;
			}

			final Area area = Area.getArea(((Player) sender).getLocation().getChunk());
			
			if (args.length == 0) {
				sender.sendMessage(Resources.PRIMARY_COLOR + "Voici les commandes de " + Resources.SECONDARY_COLOR + "LightProtect" + Resources.PRIMARY_COLOR + " :\n - " + Resources.SECONDARY_COLOR + "/" + label + " claim " + Resources.PRIMARY_COLOR + ": Verrouille un terrain " + Resources.SECONDARY_COLOR + "(astuce : F3 + G)" + Resources.PRIMARY_COLOR + "\n - " + Resources.SECONDARY_COLOR + "/" + label + " unclaim " + Resources.PRIMARY_COLOR + ": Lib�re un terrain\n - " + Resources.SECONDARY_COLOR + "/" + label + " info " + Resources.PRIMARY_COLOR + ": Affiche les informations d'un chunk\n - " + Resources.SECONDARY_COLOR + "/" + label + " set2 [joueur] " + Resources.PRIMARY_COLOR + ": Donne au joueur le statut admin\n - " + Resources.SECONDARY_COLOR + "/" + label + " set1 [joueur] " + Resources.PRIMARY_COLOR + ": Donne au joueur le statut membre\n - " + Resources.SECONDARY_COLOR + "/" + label + " remove [joueur] " + Resources.PRIMARY_COLOR + ": Lib�re un terrain");
				return true;

			} else if (args.length == 1) {
				if (args[0].equalsIgnoreCase("claim") || args[0].equalsIgnoreCase("c")) {
					if (!sender.hasPermission("lightprotect.claim")) {
						sender.sendMessage(Resources.ERROR_COLOR + "Erreur :" + Resources.PRIMARY_COLOR + " Vous n'avez pas la permission.");
						return true;
					}
					
					if (area.get3() != null) {
						sender.sendMessage(Resources.ERROR_COLOR + "Erreur :" + Resources.PRIMARY_COLOR + " Le chunk est verrouill�.");
						return true;
					}

					area.set3((OfflinePlayer) sender);
					sender.sendMessage(Resources.PRIMARY_COLOR + "Le chunk " + Resources.SECONDARY_COLOR + area.identifier + Resources.PRIMARY_COLOR + " a �t� verrouill�.");
					return true;
				} else if (args[0].equalsIgnoreCase("unclaim") || args[0].equalsIgnoreCase("uc")) {
					if (area.get3() == null) {
						sender.sendMessage(Resources.ERROR_COLOR + "Erreur :" + Resources.PRIMARY_COLOR + " Le chunk est libre.");
						return true;
					}

					if (!area.get3().equals((OfflinePlayer) sender)) {
						sender.sendMessage(Resources.ERROR_COLOR + "Erreur :" + Resources.PRIMARY_COLOR + " Vous n'�tes pas propri�taire du chunk.");
						return true;
					}

					area.set3(null);
					sender.sendMessage(Resources.PRIMARY_COLOR + "Le chunk " + Resources.SECONDARY_COLOR + area.identifier + Resources.PRIMARY_COLOR + " a �t� lib�r�.");
					return true;
				} else if (args[0].equalsIgnoreCase("info") || args[0].equalsIgnoreCase("i")) {
					if (area.get3() == null)
						sender.sendMessage(Resources.SECONDARY_COLOR + "-----" + Resources.PRIMARY_COLOR + " Chunk '" + area.identifier + "' " + Resources.SECONDARY_COLOR + "-----\n" + Resources.PRIMARY_COLOR + "Le chunk n'est pas prot�g�");
					else {
						String users2 = "";
						for (final OfflinePlayer player : area.get2())
							users2 += Resources.SECONDARY_COLOR + player.getName() + Resources.PRIMARY_COLOR + ", ";
						if (!users2.isEmpty())
							users2 = users2.substring(0, users2.length() - 2);

						String users1 = "";
						for (final OfflinePlayer player : area.get1())
							users1 += Resources.SECONDARY_COLOR + player.getName() + Resources.PRIMARY_COLOR + ", ";
						if (!users1.isEmpty())
							users1 = users1.substring(0, users1.length() - 2);

						sender.sendMessage(Resources.SECONDARY_COLOR + "-----" + Resources.PRIMARY_COLOR + " Chunk '" + area.identifier + "' " + Resources.SECONDARY_COLOR + "-----\n" + Resources.PRIMARY_COLOR + " - Votre niveau de permission : " + Resources.SECONDARY_COLOR + area.permissionLevel((OfflinePlayer) sender) + "\n" + Resources.PRIMARY_COLOR + " - Propri�taire : " + Resources.SECONDARY_COLOR + area.get3().getName() + "\n" + Resources.PRIMARY_COLOR + " - Administrateurs : " + users2 + "\n" + Resources.PRIMARY_COLOR + " - Membres : " + users1);
					}

					return true;
				}
			} else if (args.length == 2)
				if (args[0].equalsIgnoreCase("set2") || args[0].equalsIgnoreCase("2")) {
					if (area.get3() == null)
						sender.sendMessage(Resources.ERROR_COLOR + "Erreur :" + Resources.PRIMARY_COLOR + " Le chunk est libre.");
					else if (area.permissionLevel((OfflinePlayer) sender) >= 2) {
						final OfflinePlayer cible = Bukkit.getOfflinePlayer(args[1]);

						if (cible == null) {
							sender.sendMessage(Resources.ERROR_COLOR + "Erreur :" + Resources.PRIMARY_COLOR + " Le joueur " + Resources.SECONDARY_COLOR + args[1] + Resources.PRIMARY_COLOR + " n'existe pas.");
							return true;
						}

						if (area.permissionLevel(cible) == 3)
							sender.sendMessage(Resources.ERROR_COLOR + "Erreur : " + Resources.SECONDARY_COLOR + args[1] + Resources.PRIMARY_COLOR + " est le propri�taire du chunk.");
						else if (area.permissionLevel(cible) == 2)
							sender.sendMessage(Resources.ERROR_COLOR + "Erreur : " + Resources.SECONDARY_COLOR + args[1] + Resources.PRIMARY_COLOR + " est d�j� administrateur du chunk.");
						else if (area.permissionLevel(cible) == 1) {
							area.remove(cible);
							area.set2(cible);
							sender.sendMessage(Resources.SECONDARY_COLOR + args[1] + Resources.PRIMARY_COLOR + " est d�sormais administrateur du chunk.");
						} else {
							area.set2(cible);
							sender.sendMessage(Resources.SECONDARY_COLOR + args[1] + Resources.PRIMARY_COLOR + " est d�sormais administrateur du chunk.");
						}
					} else
						sender.sendMessage(Resources.ERROR_COLOR + "Erreur :" + Resources.PRIMARY_COLOR + " Vous n'avez pas la permission.");

					return true;
				} else if (args[0].equalsIgnoreCase("set1") || args[0].equalsIgnoreCase("1")) {
					if (area.get3() == null)
						sender.sendMessage(Resources.ERROR_COLOR + "Erreur :" + Resources.PRIMARY_COLOR + " Le chunk est libre.");
					else if (area.permissionLevel((OfflinePlayer) sender) >= 2) {
						final OfflinePlayer cible = Bukkit.getOfflinePlayer(args[1]);

						if (cible == null) {
							sender.sendMessage(Resources.ERROR_COLOR + "Erreur : " + Resources.PRIMARY_COLOR + " Le joueur " + Resources.SECONDARY_COLOR + args[1] + Resources.PRIMARY_COLOR + " n'existe pas.");
							return true;
						}

						if (area.permissionLevel(cible) == 3)
							sender.sendMessage(Resources.ERROR_COLOR + "Erreur : " + Resources.SECONDARY_COLOR + args[1] + Resources.PRIMARY_COLOR + " est le propri�taire du chunk.");
						else if (area.permissionLevel(cible) == 2) {
							area.remove(cible);
							area.set1(cible);
							sender.sendMessage(Resources.SECONDARY_COLOR + args[1] + Resources.PRIMARY_COLOR + " est d�sormais membre du chunk.");
						} else if (area.permissionLevel(cible) == 1)
							sender.sendMessage(Resources.ERROR_COLOR + "Erreur : " + Resources.SECONDARY_COLOR + args[1] + Resources.PRIMARY_COLOR + " Le joueur est d�j� membre du chunk.");
						else {
							area.set1(cible);
							sender.sendMessage(Resources.SECONDARY_COLOR + args[1] + Resources.PRIMARY_COLOR + " est d�sormais membre du chunk.");
						}
					} else
						sender.sendMessage(Resources.ERROR_COLOR + "Erreur :" + Resources.PRIMARY_COLOR + " Vous n'avez pas la permission.");

					return true;
				} else if (args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("r")) {
					if (area.get3() == null)
						sender.sendMessage(Resources.ERROR_COLOR + "Erreur :" + Resources.PRIMARY_COLOR + " Le chunk est libre.");
					else if (area.permissionLevel((OfflinePlayer) sender) >= 2) {
						final OfflinePlayer cible = Bukkit.getOfflinePlayer(args[1]);

						if (cible == null) {
							sender.sendMessage(Resources.ERROR_COLOR + "Erreur :" + Resources.PRIMARY_COLOR + " Le joueur " + Resources.SECONDARY_COLOR + args[1] + Resources.PRIMARY_COLOR + " n'existe pas.");
							return true;
						}

						if (area.permissionLevel(cible) == 3)
							sender.sendMessage(Resources.ERROR_COLOR + "Erreur : " + Resources.SECONDARY_COLOR + args[1] + Resources.PRIMARY_COLOR + " est le propri�taire du chunk.");
						else if (area.permissionLevel(cible) == 2) {
							area.remove(cible);
							sender.sendMessage(Resources.SECONDARY_COLOR + args[1] + Resources.PRIMARY_COLOR + " a �t� retir� du chunk.");
						} else if (area.permissionLevel(cible) == 1) {
							area.remove(cible);
							sender.sendMessage(Resources.SECONDARY_COLOR + args[1] + Resources.PRIMARY_COLOR + " a �t� retir� du chunk.");
						} else
							sender.sendMessage(Resources.ERROR_COLOR + "Erreur :" + Resources.SECONDARY_COLOR + args[1] + Resources.PRIMARY_COLOR + " n'est pas ajout� dans le chunk.");
					} else
						sender.sendMessage(Resources.ERROR_COLOR + "Erreur :" + Resources.PRIMARY_COLOR + " Vous n'avez pas la permission.");

					return true;
				}
			
			sender.sendMessage(Resources.ERROR_COLOR + "Erreur :" + Resources.PRIMARY_COLOR + " Syntaxe incorrecte.");
			return true;
		}

		return false;
	}
}
