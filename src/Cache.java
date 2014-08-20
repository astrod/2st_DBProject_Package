import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;


public class Cache {
	LinkedList<Record> list;
	Map<Integer, Record> map;
	static int MAXSIZE = 700;
	
	Cache() {
		this.list = new LinkedList<>();
		this.map = new HashMap<>();
	}

	public Record lookUp(int k) {
		Record r = map.get(k);
		if(r == null) {
			return r;
		} else {
			list.remove(r);
			list.push(r);
			return r;
		}
	}

	public void store(Record r) {
		if(list.size() >= MAXSIZE) {
			Record old = list.removeLast();
			map.remove(old.k);
		}
		this.list.push(r);
		this.map.put(r.k, r);
	}
}
