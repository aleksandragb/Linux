import java.lang.reflect.Array;
import java.nio.file.Path;
import java.util.Arrays;

public class Linux {
    Directory root;
    Directory currentDir;
    String currentPath;

    public Linux() {
        this.root = new Directory("root");
        this.currentDir = root;
        this.currentPath = "/root";

        this.executeCommand("mkdir dev");
        this.executeCommand("mkdir usr");
        this.executeCommand("mkdir /root/usr/admin");

        Directory docs = new Directory("docs");
        File plik = new File("file.txt");
        plik.setContent("Zawartość pliku file.txt xd");
        docs.addElement(plik);
        root.addElement(docs);
    }

    public String getCurrentPath(){
        return currentPath;
    }

    public void ls() {
        for (Component component : this.currentDir.getChildren()) {
            System.out.println(component.getName());
        }
    }

    public void cd(String[] tokens) {
        String pathOrName = tokens[1];

        if (tokens[1].equals("..")) {
            if (!currentPath.equals("/root")) {
                String path = currentPath.substring(1);
                String[] dirNames = path.split("/");
                String[] dirNamesWithoutLastElement = Arrays.copyOfRange(dirNames, 0, dirNames.length - 1);
                Directory lookedDir = findByPath(dirNamesWithoutLastElement);

                if (lookedDir != null) {
                    currentDir = lookedDir;
                    currentPath = "/" + String.join("/", Arrays.copyOfRange(dirNames, 0, dirNames.length - 1));
                } else {
                    System.out.println("Nie można wykonać operacji");
                }
            }
            return;
        }

        if (!pathOrName.startsWith("/")) {
            for (int i = 0; i < currentDir.getChildren().size(); i++) {
                Component component = currentDir.getChildren().get(i);

                if (component instanceof Directory && component.getName().equals(tokens[1])) {
                    currentDir = (Directory) component;
                    currentPath += ("/" + component.getName());
                    return;
                }
            }
            System.out.println("Nie ma takiego podkatalogu");
        } else {
            String path = tokens[1].substring(1);
            String[] dirNames = path.split("/");

            Directory lookedDir = findByPath(dirNames);
            if (lookedDir == null) {
                System.out.println("Niepoprawna ścieżka");
                return;
            } else {
                currentDir = lookedDir;
                currentPath = pathOrName;
            }


        }
    }


    public void mkdir(String[] tokens) {
        String pathOrName = tokens[1];
        if (pathOrName.contains("$")) {
            System.out.println("Niedozwolony znak w nazwie");
            return;
        }
        if (!pathOrName.startsWith("/")) {
            Directory newDirectory = new Directory(tokens[1]);
            currentDir.addElement(newDirectory);
        } else {
            String path = tokens[1].substring(1);
            String[] dirNames = path.split("/");
            String newName = dirNames[dirNames.length-1];
            String[] dirNamesWithoutLastElement = Arrays.copyOfRange(dirNames, 0, dirNames.length-1);
            Directory lookedDir = findByPath(dirNamesWithoutLastElement);

            if (lookedDir == null) {
                System.out.println("Niepoprawna ścieżka");
                return;
            } else {
                Directory newDirecatory = new Directory(newName);
                lookedDir.addElement(newDirecatory);
            }
        }
    }
    public void touch(String[] tokens){
        Directory dirToAddFileIn = null;
        String newFileName;

        String pathOrName = tokens[1];

        if (!pathOrName.startsWith("/")) {
            dirToAddFileIn = currentDir;
            newFileName = tokens[1];
        } else {
            String path = tokens[1].substring(1);
            String[] dirNames = path.split("/");
            newFileName = dirNames[dirNames.length-1];
            String[] dirNamesWithoutLastElement = Arrays.copyOfRange(dirNames, 0, dirNames.length-1);
            Directory lookedDir = findByPath(dirNamesWithoutLastElement);

            if (lookedDir == null) {
                System.out.println("Niepoprawna ścieżka");
                return;
            } else {
                dirToAddFileIn = lookedDir;

            }
        }
        if(newFileName.contains("$")){
            System.out.println("Niedozwolony znak w nazwie");
            return;
        }
        File newFile = new File(newFileName);
        dirToAddFileIn.addElement(newFile);
    }


    public void more(String[] tokens){
        String pathOrName = tokens[1];
        if (!pathOrName.startsWith("/")) {
            for (int i = 0; i < currentDir.getChildren().size(); i++) {
                Component component = currentDir.getChildren().get(i);

                if (component instanceof File && component.getName().equals(tokens[1])) {
                    File lookedFile = (File) component;
                    System.out.println(lookedFile.getName());
                    System.out.println(lookedFile.getContent());
                    return;
                }
            }
            System.out.println("Nie ma takiego pliku");
        } else {
            String path = tokens[1].substring(1);
            String[] dirNames = path.split("/");
            String lookedFileName = dirNames[dirNames.length-1];
            String[] dirNamesWithoutLastElement = Arrays.copyOfRange(dirNames, 0, dirNames.length-1);
            Directory lookedDir = findByPath(dirNamesWithoutLastElement);

            if (lookedDir == null) {
                System.out.println("Niepoprawna ścieżka");
                return;
            } else {
                for (int i = 0; i < lookedDir.getChildren().size(); i++) {
                    Component component = lookedDir.getChildren().get(i);

                    if (component instanceof File && component.getName().equals(lookedFileName)) {
                        File lookedFile = (File) component;
                        System.out.println("Wyświetlam zawartość pliku: " + lookedFile.getName());
                        System.out.println(lookedFile.getContent());
                        return;
                    }
                }
                System.out.println("Nie ma takiego pliku");
            }
        }
    }

public void mv(String[] tokens) {
    String sourceName = tokens[1];
    String destination = tokens[2];

    if (!destination.startsWith("/")) {
        Component component = findComponentInDirectory(currentDir, sourceName);
        if (component == null) {
            System.out.println("Nie ma takiego pliku lub katalogu");
            return;
        }

        String newFileName = destination.equals(".") ? sourceName : destination;
        component.rename(newFileName);
    } else {
        Directory dirToMoveIn;

        String path = destination.substring(1);
        String[] dirNames = path.split("/");
        Directory lookedDir = findByPath(dirNames);

        if (lookedDir == null) {
            System.out.println("Niepoprawna ścieżka");
            return;
        }

        dirToMoveIn = lookedDir;

        if (!sourceName.startsWith("/")) {
            Component component = findComponentInDirectory(currentDir, sourceName);
            if (component == null) {
                System.out.println("Nie ma takiego pliku lub katalogu");
                return;
            }
            currentDir.removeElement(sourceName);
            dirToMoveIn.addElement(component);
        } else {
            String sourcePath = sourceName.substring(1);
            String[] sourceDirNames = sourcePath.split("/");
            String sourceComponentName = sourceDirNames[sourceDirNames.length - 1];
            String[] sourceDirNamesWithoutLastElement = Arrays.copyOfRange(sourceDirNames, 0, sourceDirNames.length - 1);
            Directory sourceLookedDir = findByPath(sourceDirNamesWithoutLastElement);

            if (sourceLookedDir == null) {
                System.out.println("Niepoprawna ścieżka");
                return;
            }

            Component component = findComponentInDirectory(sourceLookedDir, sourceComponentName);
            if (component == null) {
                System.out.println("Nie ma takiego pliku lub katalogu");
                return;
            }

            sourceLookedDir.removeElement(sourceComponentName);
            dirToMoveIn.addElement(component);
        }
    }
}

    public void cp(String[] tokens) {
        String sourceName = tokens[1];
        String destination = tokens[2];

        Directory dirToMoveIn = null;

        //katalog do którego przenoszę
        if (!destination.startsWith("/")) {
            Component component = findComponentInDirectory(currentDir, destination);
            if (component == null) {
                System.out.println("Nie ma takiego katalogu");
                return;
            }
            if (!(component instanceof Directory)) { //tzn że jest plikiem
                System.out.println("Nie ma takiego katalogu");
                return;
            }
            dirToMoveIn = (Directory) component;
        }else{
            String path = destination.substring(1); //usuwam pierwszy element stringa (/)
            String[] dirNames = path.split("/");
            Directory lookedDir = findByPath(dirNames);
            if(lookedDir == null) {
                System.out.println("Nie ma takiego katalogu");
                return;
            }
            dirToMoveIn = lookedDir;
        }

        //rzecz którą przenoszę
        if (!sourceName.startsWith("/")) {
            Component component = findComponentInDirectory(currentDir, sourceName);
            if(component == null){
                System.out.println("Nie ma takiego pliku");
                return;
            }else{
                Component copiedComponent = component.copyYourself();
                dirToMoveIn.addElement(copiedComponent);
            }
        }else{
            String path = sourceName.substring(1);
            String[] dirNames = path.split("/");
            String componentToMoveName = dirNames[dirNames.length-1]; //ostatni element tablicy, który chce skopiować
            String[] dirNamesWithoutLastElement = Arrays.copyOfRange(dirNames, 0, dirNames.length-1); //tworze tablice bez ostatniego elementu
            Directory lookedDir = findByPath(dirNamesWithoutLastElement); //szukam przed-ostatniego katalogu ze ścieżki

            if (lookedDir == null) {
                System.out.println("Niepoprawna ścieżka");
                return;
            } else {
                Component component = findComponentInDirectory(lookedDir, componentToMoveName);
                if (component == null) {
                    System.out.println("Nie ma takiego pliku");
                    return;
                } else {
                    Component copiedComponent = component.copyYourself();
                    dirToMoveIn.addElement(copiedComponent);
                }
            }
        }

    }

    public Directory findByPath(String[] dirNames) { //sprawdza czy ścieżka jest git
        Directory curDir = this.root;

        for (int i = 1; i < dirNames.length; i++) { // zaczynamy od drugiego elementu pomijając root
            String dirName = dirNames[i];
            boolean found = false;

            for (Component component : curDir.getChildren()) { // szukaj danego katalogu w katalogu w którym obecnie się znajdujesz
                if (component instanceof Directory && component.getName().equals(dirName)) {
                    curDir = (Directory) component;
                    found = true;
                    break;
                }
            }


            if (!found) { // jeśli nie znaleziono katalogu zwróć null
                System.out.println("Nie znaleziono katalogu: " + dirName);
                return null;
            }
        }

        return curDir;
    }

    public Component findComponentInDirectory(Directory directory, String name) { //podaję jeden katalog lub plik i nazwe -jezeli jest to go zwraca
        for(Component component : directory.getChildren()) {
            if (component.getName().equals(name)) {
                return component;
            }
        }
        return null;
    }


    public void executeCommand(String command) {
        String[] tokens = command.split("\\s+");
        String cmd = tokens[0];

        switch (cmd) {
            case "touch":
            touch(tokens);
                break;
            case "mkdir":
                mkdir(tokens);
                break;
            case "cd":
                cd(tokens);
                break;
            case "ls":
                ls();
                break;
            case "more":
                more(tokens);
                break;
            case "mv":
                mv(tokens);
                break;
            case "cp":
                cp(tokens);
                break;
            default:
                System.out.println("Nieznana komenda: " + cmd);
        }
    }
}

