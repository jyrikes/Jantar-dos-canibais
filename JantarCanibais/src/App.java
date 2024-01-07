import java.util.ArrayList;

public class App {
    public static void main(String[] args) throws Exception {
        System.out.println("Iniciando o programa");
        Rio rio = new Rio();
        Barco barco = new Barco(rio);

        ArrayList<Thread> threads = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            Missionario missionario = new Missionario("Missionario " + i, barco, rio);
            Canibal canibal = new Canibal("Canibal " + i, barco, rio);

            rio.margemEsquerda.add(missionario);
            rio.margemEsquerda.add(canibal);

            Thread threadMissionario = new Thread(missionario);
            Thread threadCanibal = new Thread(canibal);

            threads.add(threadMissionario);
            threads.add(threadCanibal);
        }

        // Agora que todas as pessoas foram criadas, iniciar as threads
        for (Thread thread : threads) {
            thread.start();
        }

        // Aguardar todas as threads terminarem
        for (Thread thread : threads) {
            thread.join();
        }
    }
}
