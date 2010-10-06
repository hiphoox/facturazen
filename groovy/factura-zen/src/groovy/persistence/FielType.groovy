package persistence

import org.hibernate.type.BlobType
import org.hibernate.Hibernate
import java.security.PrivateKey
import java.sql.PreparedStatement
import java.sql.ResultSet

class FielType extends BlobType {

	Class getReturnedClass() {
		PrivateKey.class
	}

	boolean isMutable() {
		false
	}

	Object deepCopy(Object value) {
		//TODO
		value
	}

	Object assemble(Serializable cached, Object owner) {
		cached
	}

	Serializable disassemble(Object value) {
		value
	}

	boolean equals(Object x, Object y) {
		if (x == y || (x == null && y == null)) return true
		if (x == null || y == null) return false
		x.equals(y)
	}

	int hashCode(Object x) {
		x.hashCode()
	}

	PrivateKey nullSafeGet(ResultSet rs, String[] names, Object owner) {
		byte[] value = rs.getBlob(names[0])
		if (value) {
			//TODO
		} else {
			null
		}
	}

	void nullSafeSet(PreparedStatement st, Object value, int index) {
		if (value == null) {
			st.setNull(index, Hibernate.BLOB.sqlType())
		} else {
			st.setBlob(index, value.getEncoded())
		}
	}

	Object replace(Object orig, Object target, Object owner) {
		orig
	}

}
