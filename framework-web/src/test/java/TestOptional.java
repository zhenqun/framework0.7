import java.util.Optional;

/**
 * @author toquery
 * @version 1
 */
public class TestOptional {
    public static void main(String[] args) {
        Optional<String> anyAllowerDomainOptional = Optional.ofNullable("123");
        System.out.println(anyAllowerDomainOptional.isPresent());
        if (anyAllowerDomainOptional.isPresent()) {
            System.out.println(anyAllowerDomainOptional.get());
        }
    }
}
