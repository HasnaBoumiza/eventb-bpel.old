package vutshila.labs.bpelgen.core;

import java.util.ArrayList;

import org.eventb.core.IEvent;
import org.eventb.core.IMachineRoot;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

import vutshila.labs.bpelgen.core.eventb.EventBEvent;

/**
 * Maps RODIN MachineRoot
 * 
 * @author Ernest Mashele<mashern@tuks.co.za>
 * 
 */
public class Machine {

    private IMachineRoot machine;
    private String name;
    private IRodinProject rproject;
    private boolean isAccurate;

    /**
     * Initialise the class
     * 
     * @param machine
     */
    public void init(IMachineRoot machine) {

	this.machine = machine;
	name = machine.getElementName();
	rproject = machine.getRodinProject();

    }

    public EventBEvent[] getEvents() {

	IEvent events[];
	ArrayList<EventBEvent> eventbEvents = new ArrayList<EventBEvent>(0);

	try {
	    events = machine.getEvents();

	    for (IEvent event : events) {

	    }
	} catch (RodinDBException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

	EventBEvent[] list = new EventBEvent[eventbEvents.size()];
	return eventbEvents.toArray(list);
    }
}
