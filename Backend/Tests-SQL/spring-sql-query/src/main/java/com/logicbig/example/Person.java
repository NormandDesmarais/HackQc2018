package com.logicbig.example;

public class Person {
    private int id;
    private int psw;
    private int salt;
    private String name;
    private String username;
        
    
   @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", psw='" + psw + '\'' +
                ", salt='" + salt + '\'' +
                ", name='" + name + '\'' +
                ", username='" + username + '\'' +
                '}';
    }

    public static Person create(int id, int psw, int salt, String name, String username) {
        Person person = new Person();
        
        person.setId(id);
        person.setPsw(psw);
        person.setName(name);
        person.setUsername(username);
        return person;
        
    }

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getPsw() {
		return psw;
	}

	public void setPsw(int psw) {
		this.psw = psw;
	}

	public int getSalt() {
		return salt;
	}

	public void setSalt(int salt) {
		this.salt = salt;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
    
    
}