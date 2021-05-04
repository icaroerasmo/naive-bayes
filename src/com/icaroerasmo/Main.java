package com.icaroerasmo;

import java.util.HashMap;
import java.util.Map;

import com.icaroerasmo.data.Database;

public class Main {

	public static void main(String[] args) {
		
		Map<String, String> conjuntoTeste = new HashMap<>();
		conjuntoTeste.put("tempo", "ensolarado");
		conjuntoTeste.put("temperatura", "moderada");
		conjuntoTeste.put("umidade", "alta");
		conjuntoTeste.put("vento", "forte");
		
		System.out.println("Devo jogar tênis?");
		
		Database db = Database.carregaDatabase("./database.csv");
		
		String result = "";
		Double resultVal = 0D;
		for(String rotulo : db.valoresUnicosRotulo()) {
			var probabilidade = db.calcularProbabilidadesRotulo(rotulo); 
			for(String chave : conjuntoTeste.keySet()) {
				probabilidade *= db.calcularProbabilidadesCondicionais(
						chave, conjuntoTeste.get(chave), db.getRotulo(), rotulo);
			}
			
			if(probabilidade > resultVal) {
				result = rotulo;
				resultVal = probabilidade;
			}
		}
		
		System.out.println(result);
	}
}
