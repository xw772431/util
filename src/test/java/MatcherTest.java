import matcher.ExtendedMatcher;
import matcher.KMPMatcher;
import matcher.StringMatcher;
import org.junit.Test;

public class MatcherTest {
    @Test
    public void test() {
        ExtendedMatcher matcher = new KMPMatcher();
        String str = "aaaaaaabaaaacaaaaad";
        String ptr = "aaaa";
        StringMatcher.MatchCallback<?> callback = new ExtendedMatcher.ListIndexCallback();
        matcher.matchReversely(str, ptr, callback);
        System.out.println(callback.get());
        System.out.println(matcher.matchFirst(str, ptr, false));
    }
}
