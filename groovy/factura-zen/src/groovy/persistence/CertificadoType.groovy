package persistence

import org.hibernate.type.BlobType
import org.hibernate.Hibernate
import javax.security.cert.X509Certificate
import java.sql.PreparedStatement
import java.sql.ResultSet

class CertificadoType extends BlobType {

	Class getReturnedClass() {
		X509Certificate.class
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
		false //TODO
	}

	int hashCode(Object x) {
		x.hashCode()
	}

	X509Certificate nullSafeGet(ResultSet rs, String[] names, Object owner) {
		byte[] value = rs.getBlob(names[0])
		if (value) {
			X509Certificate.getInstance(value)
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
