package fwf.app;

public record Country(String capital, String code, String continent, String flag1x3, String flag4x3, boolean iso,
                String name) implements Comparable<Country> {
        @Override
        public int compareTo(Country o) {
                return name.compareTo(o.name());
        }

        @Override
        public boolean equals(Object other) {
                if (other instanceof Country country) {
                        return code.equals(country.code);
                }
                return false;
        }

        @Override
        public int hashCode() {
                return code.hashCode();
        }
}
