package main;

import java.io.FileNotFoundException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Athens {
    public static void main(String[] args) {
        try {
            FileReader.readFile();
        }
        catch(FileNotFoundException e) {
            System.err.println("Não existe o diretório de dados /data. Por favor, crie e coloque os arquivos necessários.");
            System.exit(1);
        }

        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        CompletableFuture<Void> filosofos = CompletableFuture.runAsync(
                () -> PhilosopherMain.filosofos(executor), executor);

        CompletableFuture.allOf(filosofos).join();

        Util.titlePrinted = false;

        CompletableFuture<Void> sofistas = CompletableFuture.runAsync(
                () -> SophistMain.sofistas(executor), executor);

        CompletableFuture.allOf(sofistas).join();

        executor.shutdown();
        try {
            boolean _ = executor.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            System.err.println("Processo interrompido: " + e.getMessage());
        }
    }

}
