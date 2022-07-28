package om.self.task.structure;

import org.apache.commons.lang3.NotImplementedException;

public abstract class NamedKeyedStructure<NAME, K, V> extends KeyedStructure<K, V>{
    NAME name;

    public void setName(NAME name) {
        this.name = name;
    }

    public NAME getName() {
        return name;
    }

    @Override
    public void attachParent(V parent) {
        try {
            super.attachParent((K)name, parent);
        }catch (Exception e){
            throw new NotImplementedException();
        }
    }
}
