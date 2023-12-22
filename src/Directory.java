import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

class Directory implements Component {
    private String name;
    private ArrayList<Component> children;

    public Directory(String name) {
        this.name = name;
        this.children = new ArrayList<>();
    }

    public void addComponent(Component component) {
        children.add(component);
    }
    @Override
    public String getName() {
        return name;
    }

    public void addElement(Component component) {
        this.children.add(component);
    }

    public void display() {
        System.out.println("Directory: " + name);
    }

    public void more() {
        System.out.println("more command cannot be applied to a directory.");
    }

    public ArrayList<Component> getChildren() {
        return children;
    }

    public void removeElement(String name) {
        for(int i = 0; i<this.children.size(); i++) {
            if (this.children.get(i).getName().equals(name)) {
                this.children.remove(i);
            }
        }
    }

    @Override
    public Component copyYourself(){
        Directory newDirectory = new Directory(this.name); //on będzie kopią

        for(Component child : this.children){ //przechodzi rekursywnie po wszystkich dzieciakach kopiowanego katalogu i używa na nich tej samej funkcji copy
            newDirectory.addElement(child.copyYourself()); //tworzy kopie dzieci i dzieci dzieci itd
        }

        return newDirectory;
    }
    @Override
    public void rename(String newName) {
        this.name = newName;
    }
}