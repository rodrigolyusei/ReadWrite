package main;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import sophist.DumbInfo;
import sophist.Sophists;

public class SophistMain {
    private static int concluded = 0;
    private static final HashMap<Integer, Long> valores = new HashMap<>();

    public static void main(String[] args) {
        try {
            FileReader.readFile();
        }
        catch(FileNotFoundException e) {
            System.err.println("Não existe o diretório de dados /data. Por favor, crie e coloque os arquivos necessários.");
        }

        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        CompletableFuture<Void> filosofos = CompletableFuture.runAsync(
                () -> sofistas(executor), executor);

        CompletableFuture.allOf(filosofos).join();

        executor.shutdown();
        try {
            boolean _ = executor.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            System.err.println("Processo interrompido: " + e.getMessage());
        }
    }

    public static void sofistas(ExecutorService executor){
        List<CompletableFuture<Void>> tasks = new ArrayList<>();

        long initialTime = System.currentTimeMillis();

        Util.printProgressBar("Sofistas", 0, 100);
        for (int i = 0; i <= 100; i++) {
            int readerCount = i;

            CompletableFuture<Void> task = CompletableFuture.runAsync(
                    () -> rodarSofista(readerCount), executor);

            tasks.add(task);
        }

        CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0])).join();

        long endTime = System.currentTimeMillis();
        long tempo = endTime - initialTime;

        System.out.println();
        Util.writeToFile(valores, "sofistas.txt", tempo);
    }

    public static void rodarSofista(int readerCount){
        long total = 0;

        for (int j = 0; j < 50; j++) {
            DumbInfo info = new DumbInfo(FileReader.data);
            Sophists phi = new Sophists();

            // inicializa o array de filósofos;
            phi.init(readerCount, info);

            // Inicia Threads
            long initialTime = System.currentTimeMillis();
            phi.runAll();

            // Espera terminar
            phi.waitAll();
            long endTime = System.currentTimeMillis();
            long tempo = endTime - initialTime;
            total += tempo;
        }

        long media = total / 50;
        synchronized (valores) {
            valores.put(readerCount, media);
        }
        concluded++;
        Util.printProgressBar("Sofistas", concluded, 100);
    }
}
