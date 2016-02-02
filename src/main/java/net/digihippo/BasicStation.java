package net.digihippo;

import java.io.Serializable;

final class BasicStation implements Station, Serializable {
    private final String threeLetterCode;
    private final String fullname;

    BasicStation(String threeLetterCode, String fullname) {
        this.threeLetterCode = threeLetterCode;
        this.fullname = fullname;
    }

    @Override
    public String threeLetterCode() {
        return threeLetterCode;
    }

    @Override
    public String fullName() {
        return fullname;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BasicStation that = (BasicStation) o;

        if (fullname != null ? !fullname.equals(that.fullname) : that.fullname != null) return false;
        if (threeLetterCode != null ? !threeLetterCode.equals(that.threeLetterCode) : that.threeLetterCode != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = threeLetterCode != null ? threeLetterCode.hashCode() : 0;
        result = 31 * result + (fullname != null ? fullname.hashCode() : 0);
        return result;
    }
}