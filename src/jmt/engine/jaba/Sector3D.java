/**    
  * Copyright (C) 2006, Laboratorio di Valutazione delle Prestazioni - Politecnico di Milano

  * This program is free software; you can redistribute it and/or modify
  * it under the terms of the GNU General Public License as published by
  * the Free Software Foundation; either version 2 of the License, or
  * (at your option) any later version.

  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of
  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  * GNU General Public License for more details.

  * You should have received a copy of the GNU General Public License
  * along with this program; if not, write to the Free Software
  * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
  */

package jmt.engine.jaba;

import java.util.Vector;

import jmt.engine.jaba.Hull.Vertex;

/**
 * Created by IntelliJ IDEA.
 * User: Andrea Zanzottera
 * Date: 27-lug-2005
 * Time: 13.54.43
 */
public class Sector3D {

	private int type; // 3=triangolo

	private Vector points = new Vector();
	private Vector stations = new Vector();
	private Vector xycoord = new Vector();
	private Vector statname = new Vector();
	private String sname;
	private String[] classNames;

	public Sector3D() {

	}

	public void setClassNames(String[] classNames) {
		this.classNames = classNames;
	}

	// COSTRUTTORI DI TRIANGOLI (3 STAZIONI SATURANO CONTEMPORANEAMENTE)
	public Sector3D(double b11, double b12, double b13, double b21, double b22, double b23, double b31, double b32, double b33, int thistype,
			Vertex vv1, Vertex vv2, Vertex vv3) {
		BetaVertex b1 = new BetaVertex(b11, b12, b13);
		BetaVertex b2 = new BetaVertex(b21, b22, b23);
		BetaVertex b3 = new BetaVertex(b31, b32, b33);
		points.addElement(b1);
		points.addElement(b2);
		points.addElement(b3);
		type = thistype;
		stations.addElement(vv1);
		stations.addElement(vv2);
		stations.addElement(vv3);

	}

	public Sector3D(double b11, double b12, double b13, double b21, double b22, double b23, double b31, double b32, double b33, int thistype,
			Vertex vv1, Vertex vv2, Vertex vv3, Vertex vv4) {
		BetaVertex b1 = new BetaVertex(b11, b12, b13);
		BetaVertex b2 = new BetaVertex(b21, b22, b23);
		BetaVertex b3 = new BetaVertex(b31, b32, b33);
		points.addElement(b1);
		points.addElement(b2);
		points.addElement(b3);
		type = thistype;
		stations.addElement(vv1);
		stations.addElement(vv2);
		stations.addElement(vv3);
		stations.addElement(vv4);

	}

	// COSTRUTTORI DI SETTORI IN CUI SATURANO 2 STAZIONI CONTEMPORANEAMENTE
	public Sector3D(BetaVertex v1, BetaVertex v2, BetaVertex v3, BetaVertex v4, int thistype, Vertex vv1, Vertex vv2) {
		points.addElement(v1);
		points.addElement(v2);
		points.addElement(v3);
		points.addElement(v4);
		type = thistype;
		stations.addElement(vv1);
		stations.addElement(vv2);
	}

	public Sector3D(Vector points, int thistype, Vertex vv1, Vertex vv2) {
		this.points = points;
		type = thistype;
		stations.addElement(vv1);
		stations.addElement(vv2);
	}

	public Sector3D(Vector points, int thistype, Vertex vv1, Vertex vv2, Vertex vv3, Vertex vv4) {
		this.points = points;
		type = thistype;
		stations.addElement(vv1);
		stations.addElement(vv2);
		stations.addElement(vv3);
		stations.addElement(vv4);
	}

	// COSTRUTTORE GENERICO CON VETTORI
	public Sector3D(Vector points, int thistype, Vector stations) {
		this.points = points;
		type = thistype;
		this.stations = stations;
	}

	/**                                                                     !!NON USATO!!
	 * Restituisce la coordinata "x","y" o "z" del vertice vertex nello spazio Beta
	 * @param vertex
	 * @param coord
	 * @return la coordinata "x","y" o "z" del vertice vertex nello spazio Beta
	 */
	public double getBeta(int vertex, String coord) {
		double beta = -1;
		if (coord == "x" || coord == "X") {
			beta = ((BetaVertex) points.get(vertex)).getX();
		} else if (coord == "y" || coord == "Y") {
			beta = ((BetaVertex) points.get(vertex)).getY();
		} else if (coord == "z" || coord == "Z") {
			beta = ((BetaVertex) points.get(vertex)).getZ();
		}
		return beta;
	}

	public double getBeta(int vertex, int coord) {
		double beta = -1;
		if (coord == 1) {
			beta = Math.rint((((BetaVertex) points.get(vertex)).getX()) * 1000);
			beta = beta / 1000;
		} else if (coord == 2) {
			beta = Math.rint((((BetaVertex) points.get(vertex)).getY()) * 1000);
			beta = beta / 1000;
		} else if (coord == 3) {
			beta = Math.rint((((BetaVertex) points.get(vertex)).getZ()) * 1000);
			beta = beta / 1000;
		}
		return beta;
	}

	/**
	 * Restituisce un array di double che contengono le coordinate nello spazio zeta
	 * del vertice vertex
	 * @param vertex
	 * @return un array di double che contengono le coordinate nello spazio zeta
	 */
	public double[] getBetas(int vertex) {
		double[] beta = new double[3];
		beta[0] = ((BetaVertex) points.get(vertex)).getX();
		beta[1] = ((BetaVertex) points.get(vertex)).getY();
		beta[2] = ((BetaVertex) points.get(vertex)).getZ();
		return beta;
	}

	public BetaVertex getV0() {
		return (BetaVertex) points.get(0);
	}

	public BetaVertex getV1() {
		return (BetaVertex) points.get(1);
	}

	public BetaVertex getV2() {
		return (BetaVertex) points.get(2);
	}

	public BetaVertex getV(int i) {
		return (BetaVertex) points.get(i);
	}

	public int CountPoint() {
		return points.size();
	}

	// Metodi per ritornare le stazioni di un settore
	public Vertex getS0() {
		return (Vertex) stations.get(0);
	}

	public Vertex getS1() {
		return (Vertex) stations.get(1);
	}

	public Vertex getS2() {
		return (Vertex) stations.get(2);
	}

	public Vertex getS(int i) {
		return (Vertex) stations.get(i);
	}

	public Vector getS() {
		return stations;
	}

	/**
	 * @return un int che corrisponde al numero di stazioni associati a un settore
	 */
	public int CountStations() {
		return stations.size();
	}

	/**
	 * Aggiunge un punto nello spazio Beta e la relativa stazione
	 * @param betas
	 * @param station
	 * @return il settore aggiornato
	 */
	public Sector3D AddVertex(BetaVertex betas, Vertex station) {
		points.addElement(betas);
		stations.addElement(station);
		return this;
	}

	/**
	 * Il metodo serve per settare il tipo di settore.
	 * 1=Satura una singola stazione
	 * 2=Saturano 2 stazioni
	 * 3=Saturano 3 stazioni
	 * eccetera
	 * @param type
	 * @return il settore aggiornato
	 */
	public Sector3D setType(int type) {
		this.type = type;
		return this;
	}

	/**
	 * @return il tipo di Settore
	 */
	public int getType() {
		return type;
	}

	/**
	 * Verifica se un Sector3D � un triangolo dove saturano 3 stazioni contemp.
	 * @return TRUE se � un triangolo
	 */
	public boolean IsTriangle() {
		if (type == 3) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Dato un vettore di Vertex e un Vertex associa quest'ultimo alle stazioni iniziali
	 * @param points
	 * @param p
	 * @return il numero della stazione iniziale
	 */
	public int AssoStation3D(Vector points, Vertex p) {
		int stat = -1;
		int[] pcoord = p.getCoords();

		for (int i = 0; i < points.size(); i++) {

			int[] pvect = ((Vertex) points.get(i)).getCoords();
			if ((pcoord[0] == pvect[0] && pcoord[1] == pvect[1] && pcoord[2] == pvect[2])
					|| (pcoord[0] == pvect[0] && pcoord[1] == pvect[1] && pcoord[2] == -1)
					|| (pcoord[0] == pvect[0] && pcoord[1] == -1 && pcoord[2] == pvect[2])
					|| (pcoord[0] == -1 && pcoord[1] == pvect[1] && pcoord[2] == pvect[2])

			) {
				stat = i;
			}
		}
		return stat;
	}

	////////////////////// GESTIONE DELLE COORDINATE PROIETTATE SUL PIANO XY ////////////////////

	public double[] getxycoord(int point) {
		return (double[]) xycoord.get(point);
	}

	public double getx(int point) {
		double[] xy = (double[]) xycoord.get(point);
		return xy[0];
	}

	public double gety(int point) {
		double[] xy = (double[]) xycoord.get(point);
		return xy[1];
	}

	public void addxycoord(double[] coord) {
		xycoord.addElement(coord);
	}

	//Metodo che calcola il punto a cui collegare l'etichetta di un settore

	/**
	 * Calculates central point of the polygon
	 * <br> Fixed by Bertoli Marco
	 * @return an array with x and y coordinates of center
	 */
	public double[] getCentre() {

		double[] out = new double[2];
		double xx = 0;
		double yy = 0;
		for (int i = 0; i < xycoord.size(); i++) {
			xx = xx + getx(i);
			yy = yy + gety(i);
		}
		out[0] = xx / xycoord.size();
		out[1] = yy / xycoord.size();
		return out;
	}

	/////////////////////// GESTIONE DEI NOMI DELLE STAZIONI ////////////////////////////////////

	public void givename(Vector oristations) {
		for (int i = 0; i < stations.size(); i++) {
			for (int j = 0; j < oristations.size(); j++) {
				//Controllo se due vertici sono uguali
				int[] v1 = ((Vertex) stations.get(i)).getCoords();
				int[] v2 = ((Station3D) oristations.get(j)).getV3D().getCoords();
				if ((Math.abs(v1[0] - v2[0]) < 0.001 && Math.abs(v1[1] - v2[1]) < 0.001 && Math.abs(v1[2] - v2[2]) < 0.001)
						|| (Math.abs(v1[0] - v2[0]) < 0.001 && Math.abs(v1[1] - v2[1]) < 0.001 && (v1[2] == -1 || v2[2] == -1))
						|| (Math.abs(v1[0] - v2[0]) < 0.001 && Math.abs(v1[2] - v2[2]) < 0.001 && (v1[1] == -1 || v2[1] == -1))
						|| (Math.abs(v1[2] - v2[2]) < 0.001 && Math.abs(v1[1] - v2[1]) < 0.001 && (v1[0] == -1 || v2[0] == -1))) {
					statname.addElement(((Station3D) oristations.get(j)).getName());
					//System.out.println("Nome: "+((Station3D)oristations.get(j)).getName());
					break;
				} else {
					//System.out.println("no corr: "+v1[0]+" "+v2[0]+" "+v1[1]+" "+v2[1]+" "+v1[2]+" "+v2[2]);
				}
			}
		}
		String name = "";
		String[] arname = new String[statname.size()];
		for (int i = 0; i < statname.size(); i++) {
			//rende dinamico il nome

			if (statname.size() != stations.size()) {
				String temp = "Station";
				name = name.concat(temp + " ");
			} else

			{
				//controllo sulla ripetizione del nome
				arname[i] = (String) statname.get(i);
				int k = 0;
				for (int j = 0; j < arname.length; j++) {
					if (arname[i] != arname[j]) {
						k++;
					}
				}
				//se il nome non � gi� stato usato lo si aggiunge
				if (k == arname.length - 1) {
					name = name.concat((String) statname.get(i) + " ");
				}
			}
		}
		sname = name;
	}

	public String getName() {
		return sname;
	}

	public String[] getNames() {
		String name = "";
		String[] arname = new String[statname.size()];
		for (int i = 0; i < statname.size(); i++) {
			arname[i] = (String) statname.get(i);
			int k = 0;
			for (int j = 0; j < arname.length; j++) {
				if (arname[i] != arname[j]) {
					k++;
				}
			}
		}
		return arname;
	}

	public String toString() {
		String out = "";
		{

			String name = "";
			String[] arname = new String[statname.size()];
			for (int i = 0; i < statname.size(); i++) {
				//rende dinamico il nome

				if (statname.size() != stations.size()) {
					String temp = "Station";
					name = name.concat(temp + " ");
				} else

				{
					//controllo sulla ripetizione del nome
					arname[i] = (String) statname.get(i);
					int k = 0;
					for (int j = 0; j < arname.length; j++) {
						if (arname[i] != arname[j]) {
							k++;
						}
					}
					//se il nome non � gi� stato usato lo si aggiunge
					if (k == arname.length - 1) {
						name = name.concat((String) statname.get(i) + " ");
					}
				}
			}
			String betas = "";
			for (int i = 0; i < points.size(); i++) {
				double b1 = Math.round(((BetaVertex) points.get(i)).getX() * 1000);
				double b2 = Math.round(((BetaVertex) points.get(i)).getY() * 1000);
				double b3 = Math.round(((BetaVertex) points.get(i)).getZ() * 1000);

				betas = betas.concat("<br>" + classNames[0] + ":  " + b1 / 1000 + "   " + classNames[1] + ":  " + b2 / 1000 + "   " + classNames[2]
						+ ":  " + b3 / 1000);
			}
			out = "<font size=\"4\">" + "<b>" + "Bottleneck Regions for: " + name + "</b>" + "</font><font size=\"3\">" + betas;
		}
		return out;
	}
}
