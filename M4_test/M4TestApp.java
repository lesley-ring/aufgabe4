public class M4TestApp {
    public static void main(String[] args) {
        M4ServerTest.los();
        try {
            Thread.sleep(3000);
        } catch ( Exception e) {}
        new M4ClientTest().los();
    }
}
