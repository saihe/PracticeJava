package ksaito.practice.java.outerProcess;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Application {
  public static void main(String[] args) {
    log("処理開始");
    List<String> cmd = new ArrayList<>();
    cmd.add(args.length == 0 ? "sh/cmd.sh": args[0]);
    Arrays.stream(args).skip(1).forEach(cmd::add);
    log("呼び出しコマンド[" + cmd.get(0) + "]、オプション[" + cmd.stream().skip(1).collect(Collectors.joining(" ")) + "]");

    ProcessBuilder pb = new ProcessBuilder().command(cmd);
    try {
      Process p = pb.start();
      if (p.waitFor(5, TimeUnit.SECONDS)) {
        log("外部プロセス終了");
      } else {
        log("タイムアウト");
      }
      int v = p.exitValue();
      if (p.isAlive()) {
        p.destroy();
      }
      log("外部コマンド終了コード[" + v + "]");
    } catch (IOException | InterruptedException e) {
      e.printStackTrace();
    }
    log("処理終了");
    System.exit(0);
  }

  private static void log(String message) {
    LocalDateTime dt = LocalDateTime.now();
    String dts = dt.format(DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss"));
    print(dts + " " + message);
  }

  private static void print(String message) {
    System.out.println(message);
  }
}
