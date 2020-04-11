import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Map;

public class Application {
  public static void main(String[] args) {
    try {
      InetAddress.getByName("mt-nodered.fubright.net");
    } catch (UnknownHostException e) {
      // 名前解決できなかった場合は InetAddress#negativeCache にホスト名が格納される
    }

    printCacheList(CacheType.Positive);
    printCacheList(CacheType.Negative);
  }

  public enum CacheType {
    Positive("addressCache"), Negative("negativeCache");
    private String fieldName;

    private CacheType(String name) {
      this.fieldName = name;
    }

    public String getFieldName() {
      return this.fieldName;
    }
  }

  /**
   * キャッシュされたホスト名とアドレス（名前解決に失敗した場合はキャッシュの有効期限）を出力します
   *
   * @param type Positive/Negative
   */
  @SuppressWarnings("unchecked")
  public static void printCacheList(CacheType type) {
    System.out.println(type.getFieldName());

    // Cache 型の private フィールドを取得
    Object addressCache = getField(InetAddress.class, "java.net.InetAddress", type.getFieldName());

    // キャッシュの一覧を取得
    Map<String, Object> cacheMap = (Map<String, Object>) getField(addressCache, "java.net.InetAddress$Cache", "cache");

    // 一覧表示
    for (Map.Entry<String, Object> entry : cacheMap.entrySet()) {
      String hostname = entry.getKey();
      Object cacheEntry = entry.getValue();
      switch (type) {
        case Positive:
          // 名前解決後の InetAddress オブジェクトを取得（0番目は IPv4, 1番目は IPv6）
          InetAddress[] addresses = (InetAddress[]) getField(cacheEntry, "java.net.InetAddress$CacheEntry", "addresses");
          System.out.printf("  %-20s => %s%n", hostname, addresses[0].getHostAddress());
          break;
        case Negative:
          // キャッシュの有効期限を取得
          long expiration = (long) getField(cacheEntry, "java.net.InetAddress$CacheEntry", "expiration");
          System.out.printf("  %-20s (Expiration Date: %s)%n", hostname, new Date(expiration));
          break;
      }
    }
  }

  /**
   * オブジェクト内のprivateフィールドを取得します
   *
   * @param obj       Object
   * @param className クラス名
   * @param fieldName フィールド名
   * @return obj 内の fieldName 項目
   */
  public static Object getField(Object obj, String className, String fieldName) {
    try {
      Field field = Class.forName(className).getDeclaredField(fieldName);
      field.setAccessible(true);
      return field.get(obj);
    } catch (Exception e) {
      e.getStackTrace();
      return null;
    }
  }
}
