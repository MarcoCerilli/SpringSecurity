package com.example.demo;

public class pet {
	
	String nome;
	String marca;
	double prezzo;
	String url;
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getMarca() {
		return marca;
	}
	public void setMarca(String marca) {
		this.marca = marca;
	}
	public double getPrezzo() {
		return prezzo;
	}
	public void setPrezzo(double prezzo) {
		this.prezzo = prezzo;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	@Override
	public String toString() {
		return "pet [nome=" + nome + ", marca=" + marca + ", prezzo=" + prezzo + ", url=" + url + "]";
	}
	
	

}
