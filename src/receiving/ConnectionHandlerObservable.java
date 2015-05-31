/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package receiving;

/**
 *
 * @author cleber
 */
public interface ConnectionHandlerObservable {

	public void notifyObservers();

	public boolean addObserver(ConnectionManager conMan);

	public boolean removeObserver(ConnectionManager conMan);
}
