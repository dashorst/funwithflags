package fwf.app;

public record Country(String capital, String code, String continent, String flag1x3, String flag4x3, boolean iso,
                String name) implements Comparable<Country> {
        @Override
        public int compareTo(Country o) {
                return name.compareTo(o.name());
        }
}
