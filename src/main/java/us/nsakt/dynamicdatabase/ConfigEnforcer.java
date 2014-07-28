package us.nsakt.dynamicdatabase;

import us.nsakt.dynamicdatabase.util.NsaktException;

public class ConfigEnforcer {

    public static class Documents {
        public static class Clusters {
            public static void ensureEnabled() throws NsaktException {
                if (Config.Documents.Clusters.enabled) throw new NsaktException();
            }
        }

        public static class Groups {
            public static void ensureEnabled() throws NsaktException {
                if (Config.Documents.Groups.enabled) throw new NsaktException();
            }
        }

        public static class Punishments {
            public static void ensureEnabled() throws NsaktException {
                if (Config.Documents.Punishments.enabled) throw new NsaktException();
            }
        }

        public static class Sessions {
            public static void ensureEnabled() throws NsaktException {
                if (Config.Documents.Sessions.record) throw new NsaktException();
            }
        }

        public static class Users {
            public static void ensureEnabled() throws NsaktException {
                if (Config.Documents.Users.enabled) throw new NsaktException();
            }
        }
    }

    public static class CrossServer {
        public static void ensureEnabled() throws NsaktException {
            if (Config.CrossServer.enabled) throw new NsaktException();
        }

        public static class AdminChat {
            public static void ensureEnabled() throws NsaktException {
                if (Config.CrossServer.AdminChat.enabled) throw new NsaktException();
            }
        }

        public static class Punishments {
            public static void ensureEnabled() throws NsaktException {
                if (Config.CrossServer.Punishments.enabled) throw new NsaktException();
            }
        }
    }
}
