/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package aib_server;
import java.util.Date;
/**
 *
 * @author leijurv
 */
public abstract class LogEvent {
    public abstract String toString();
    public abstract Date time();
}
