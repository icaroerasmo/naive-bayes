package com.icaroerasmo.data;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Database {
	
	private List<Tupla> tuplas;
	
	private String colunaRotulo;
	
	private Database() {}
	
	private Database(String colunaRotulo) {
		this.colunaRotulo = colunaRotulo;
		this.tuplas = new ArrayList<>();
	}

	public List<Tupla> getTuplas() {
		return tuplas;
	}

	private void setTuplas(List<Tupla> tuplas) {
		this.tuplas = tuplas;
	}
	
	public Double calcularProbabilidadesRotulo(String valor) {
		return this.calcularProbabilidadeColuna(colunaRotulo, valor, tuplas);
	}
	
	public Double calcularProbabilidadesCondicionais(
			String coluna, String valor, String colunaCond, String valorCond) {
		List<Tupla> dataset = this.tuplas.stream().
				filter(t -> t.getAsString(colunaCond).
						equals(valorCond)).
				collect(Collectors.toList());
		return calcularProbabilidadeColuna(coluna, valor, dataset);
	}
	
	public Double calcularProbabilidadeColuna(String coluna, String valor, List<Tupla> dataset) {
		
			Integer quantidade = dataset.stream().
				filter(t -> t.getAsString(coluna).
						equals(valor)).
				map(v -> 1).
				reduce((v,ac) -> v + ac).orElse(0);
		
		return ((double)quantidade)/dataset.size();
	}

	public String getRotulo() {
		return this.colunaRotulo;
	}
	
	public Set<String> valoresUnicosRotulo() {
		Set<String> valoresUnicosColunaRotulo = tuplas.stream().
				map(t -> t.getAsString(colunaRotulo)).
				collect(Collectors.toSet());
		return valoresUnicosColunaRotulo;
	}
	
	public static Database carregaDatabase(String arquivoCSV) {

		List<String> labels = new ArrayList<>();
		List<Tupla> tuplas = new ArrayList<>();
		TuplaBuilder tupla = new TuplaBuilder();

		BufferedReader br = null;
		String linha = "";
		String csvDivisor = ",";
		int index = 0;
		try {

			br = new BufferedReader(new FileReader(arquivoCSV));
			while ((linha = br.readLine()) != null) {

				String[] values = linha.split(csvDivisor);

				for (int i = 0; i < values.length; i++) {
					String value = values[i];
					if (index < 1) {
						labels.add(value);
					} else {
						try {
							Double numericValue = Double.parseDouble(value);
							tupla.add(labels.get(i), numericValue);
						} catch(NumberFormatException e) {
							tupla.add(labels.get(i), value);
						}
					}
				}
				
				if(index > 0) {
					tuplas.add(tupla.build());
				}

				index++;
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		Database db = new Database(labels.get(labels.size()-1));
		db.setTuplas(tuplas);
		return db;
	}
}
