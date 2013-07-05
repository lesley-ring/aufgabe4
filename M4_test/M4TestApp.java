import uni.prakinf.m4.server.Server;

public class M4TestApp {
    public static void main(String[] args) {
        Server server = new Server();
        server.startServer();
        try {
            Thread.sleep(3000);
        } catch (Exception e) {
        }
        new M4ClientTest().los();
        try {
            Thread.sleep(3000);
        } catch (Exception e) {
        }
        server.stopServer();
    }
}
