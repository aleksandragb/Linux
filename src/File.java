class File implements Component {
    private String name;
    private String content;


    public Component copyWithNewName(String newName) {
        File newFile = new File(newName);
        newFile.setContent(this.content);
        return newFile;
    }

    public File(String name) {
        this.name = name;
        this.content = "";
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String getName() {
        return name;
    }


    public void display() {
        System.out.println("File: " + name);
    }

    public void more() {
        System.out.println("Content of file " + name + ":");
        System.out.println(content);
    }

    @Override
    public Component copyYourself(){
        File newFile = new File(this.name);
        newFile.setContent(this.content);
        return newFile;
    }
    @Override
    public void rename(String newName) {
        this.name = newName;
    }
}
