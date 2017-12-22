package com.iota.iri.network;

import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class Neighbor {

    private final InetSocketAddress address;
    
    private long numberOfAllTransactions;
    private long numberOfNewTransactions;
    private long numberOfInvalidTransactions;
    private long randomTransactionRequests;
    private long numberOfSentTransactions;

    private int newTransactionsCounter;
    private long newTransactionsTimer;
    private final double newTransactionsLimit;
    public static final long newTransactionsWindow = 10 * 1000L;

    private boolean flagged = false;
    public boolean isFlagged() {
        return flagged;
    }
    public void setFlagged(boolean flagged) {
        this.flagged = flagged;
    }
    
    private boolean stopped = false;
    public void stop() {
        this.stopped = true;
    }
    public boolean isStopped() {
        return stopped;
    }
    private final static AtomicInteger numPeers = new AtomicInteger(0);
    public static int getNumPeers() {
        return numPeers.get();
    }
    public static void incNumPeers() {
        numPeers.incrementAndGet();
    }
    public static void decNumPeers() {
        int v = numPeers.decrementAndGet();
        if (v < 0) numPeers.set(0);
    }

    private final String hostAddress;

    public String getHostAddress() {
        return hostAddress;
    }


    public Neighbor(final InetSocketAddress address, boolean isConfigured) {
        this.address = address;
        this.hostAddress = address.getAddress().getHostAddress();
        this.flagged = isConfigured;
        this.newTransactionsLimit = 0;
    }

    public Neighbor(final InetSocketAddress address, boolean isConfigured, double limit) {
        this.address = address;
        this.hostAddress = address.getAddress().getHostAddress();
        this.flagged = isConfigured;
        this.newTransactionsLimit = (limit * newTransactionsWindow) / 1000;
    }

    public abstract void send(final DatagramPacket packet);
    public abstract int getPort();
    public abstract String connectionType();
    public abstract boolean matches(SocketAddress address);

    @Override
    public boolean equals(final Object obj) {
        return this == obj || !((obj == null) || (obj.getClass() != this.getClass())) && address.equals(((Neighbor) obj).address);
    }

    @Override
    public int hashCode() {
        return address.hashCode();
    }
    
    public InetSocketAddress getAddress() {
		return address;
	}
    
    void incAllTransactions() {
    	numberOfAllTransactions++;
    }
    
    void incNewTransactions() {
    	numberOfNewTransactions++;
        newTransactionsCounter++;
    }

    public boolean isBelowNewTransactionLimit() {
        if (newTransactionsLimit == 0) {
            return true;
        }

        long now = System.currentTimeMillis();
        if ((now - newTransactionsTimer) > newTransactionsWindow) {
            newTransactionsCounter = 0;
            newTransactionsTimer = now;
        }
        return (newTransactionsCounter < newTransactionsLimit);
    }


    void incRandomTransactionRequests() {
        randomTransactionRequests++;
    }

    public void incInvalidTransactions() {
    	numberOfInvalidTransactions++;
    }
    
    public void incSentTransactions() {
        numberOfSentTransactions++;
    }
    
    public long getNumberOfAllTransactions() {
		return numberOfAllTransactions;
	}
    
    public long getNumberOfInvalidTransactions() {
		return numberOfInvalidTransactions;
	}
    
    public long getNumberOfNewTransactions() {
		return numberOfNewTransactions;
	}

	public long getNumberOfRandomTransactionRequests() {
        return randomTransactionRequests;
    }
	
	public long getNumberOfSentTransactions() {
	    return numberOfSentTransactions;
	}
    
}
