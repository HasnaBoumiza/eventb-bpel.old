package za.vutshilalabs.bpelgen.core.translation;

/**
 * 
 * @author Ramuthiveli Lovewell
 *
 */
import java.util.LinkedList;
import java.util.List;

public class HashTable<T> {
	private static final int DEFAULT_TABLE_SIZE = 10;

	private static boolean isPrime(int n) {
		if (n == 2 || n == 3)
			return true;

		if (n == 1 || n % 2 == 0)
			return false;

		for (int i = 3; i * i <= n; i += 2)
			if (n % i == 0)
				return false;

		return true;
	}

	private static int nextPrime(int n) {
		if (n % 2 == 0)
			n++;

		for (; !isPrime(n); n += 2)
			;
		return n;
	}

	private int tableSize = 10;

	private List<T>[] table;

	private int currentSize;

	public HashTable() {
		this(DEFAULT_TABLE_SIZE);
	}

	@SuppressWarnings("unchecked")
	public HashTable(int size) {
		table = new LinkedList[nextPrime(size)];
		for (int i = 0; i < table.length; i++)
			table[i] = new LinkedList<T>();
	}

	public void clearTable() {
		for (int i = 0; i < table.length; i++) {
			table[i].clear();
		}
		currentSize = 0;
	}

	public boolean contains(String x) {
		List<T> targetList = table[hash(x)];
		return targetList.contains(x);
	}

	public List<T> getHeapLocation(String x) {
		int index = hash(x);
		return table[index];

	}

	public int hash(String key) {
		int hashVal = 0;

		for (int i = 0; i < key.length(); i++)
			hashVal = 37 * hashVal + key.charAt(i);

		hashVal %= tableSize;
		if (hashVal < 0)
			hashVal += tableSize;

		return hashVal;
	}

	public void insert(String x, T data) {
		List<T> targetList = table[hash(x)];
		if (!targetList.contains(x)) {
			targetList.add(data);
			// Rehash;
			if (++currentSize > table.length)
				rehash();
		}
	}

	private int myhash(T x) {
		int hashVal = x.hashCode();

		hashVal %= table.length;
		if (hashVal < 0)
			hashVal += table.length;

		return hashVal;
	}

	@SuppressWarnings("unchecked")
	private void rehash() {
		List<T>[] oldLists = table;

		// Create new double-sized, empty table
		table = new List[nextPrime(2 * table.length)];
		for (int j = 0; j < table.length; j++)
			table[j] = new LinkedList<T>();

		// Copy table over
		currentSize = 0;
		for (int i = 0; i < oldLists.length; i++)
			for (T item : oldLists[i])
				rehashInsert(item);
	}

	public void rehashInsert(T x) {

		List<T> whichList = table[myhash(x)];

		if (!whichList.contains(x)) {
			whichList.add(x);

			// Rehash;
			if (++currentSize > table.length)
				rehash();
		}
	}

	public void remove(T x) {
		List<T> whichList = table[myhash(x)];
		if (whichList.contains(x)) {
			whichList.remove(x);
			currentSize--;
		}
	}

}
