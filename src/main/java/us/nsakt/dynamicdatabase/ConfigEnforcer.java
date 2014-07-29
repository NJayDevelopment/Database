package us.nsakt.dynamicdatabase;

import org.bson.types.ObjectId;
import us.nsakt.dynamicdatabase.util.NsaktException;

/**
 * Utility class for checking that operations are allowed according to the configured settings.
 *
 * @author NathanTheBook
 */
public class ConfigEnforcer {

    public static class Documents {
        public static class Clusters {
            public static void ensureEnabled() throws NsaktException {
                if (!Config.Documents.Clusters.enabled) throw new NsaktException();
            }
        }

        public static class Groups {
            public static void ensureEnabled() throws NsaktException {
                if (!Config.Documents.Groups.enabled) throw new NsaktException();
            }
        }

        public static class Punishments {
            public static void ensureEnabled() throws NsaktException {
                if (!Config.Documents.Punishments.enabled) throw new NsaktException();
            }
        }

        public static class Sessions {
            public static void ensureEnabled() throws NsaktException {
                if (!Config.Documents.Sessions.record) throw new NsaktException();
            }
        }

        public static class Users {
            public static void ensureEnabled() throws NsaktException {
                if (!Config.Documents.Users.enabled) throw new NsaktException();
            }
        }
    }

    public static class CrossServer {
        public static void ensureEnabled() throws NsaktException {
            if (!Config.CrossServer.enabled) throw new NsaktException();
        }

        public static class AdminChat {
            public static void ensureEnabled() throws NsaktException {
                if (!Config.CrossServer.AdminChat.enabled) throw new NsaktException();
            }

            public static void canSend() throws NsaktException {
                if (!Config.CrossServer.AdminChat.send) throw new NsaktException();
            }

            public static void canReceive() throws NsaktException {
                if (!Config.CrossServer.AdminChat.receive) throw new NsaktException();
            }

            public static void allowedCluster(ObjectId cluster) throws NsaktException {
                if (!Config.CrossServer.AdminChat.send) throw new NsaktException();
            }
        }

        public static class Punishments {
            public static void ensureEnabled() throws NsaktException {
                if (!Config.CrossServer.Punishments.enabled) throw new NsaktException();
            }
        }
    }
}
