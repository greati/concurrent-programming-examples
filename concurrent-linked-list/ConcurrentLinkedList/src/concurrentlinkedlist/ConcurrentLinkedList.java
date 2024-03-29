/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package concurrentlinkedlist;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.NoSuchElementException;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author carlosv
 */
public class ConcurrentLinkedList {
    private volatile Node head;
    private volatile Node tail;
    private volatile int length;
    
    private final ReentrantReadWriteLock searchRemoveLock;
    private final ReentrantLock insertRemoveLock;
    private final ReentrantLock printLock;
    
    private final boolean randomDelays;
    private Random randomGenerator;
    private static final int MIN_DELAY = 100;
    private static final int MAX_DELAY = 2000;
    
    public ConcurrentLinkedList() {
        this.head = null;
        this.tail = null;
        this.length = 0;
        this.randomDelays = false;
        
        boolean fair = true;
        this.searchRemoveLock = new ReentrantReadWriteLock(fair);
        this.insertRemoveLock = new ReentrantLock(fair);
        this.printLock = new ReentrantLock(fair);
    }
    
    public ConcurrentLinkedList(boolean randomDelays) {
        this.head = null;
        this.tail = null;
        this.length = 0;
        this.randomDelays = randomDelays;
        if (randomDelays) {
            this.randomGenerator = new Random();
        }
        
        boolean fair = true;
        this.searchRemoveLock = new ReentrantReadWriteLock(fair);
        this.insertRemoveLock = new ReentrantLock(fair);
        this.printLock = new ReentrantLock(fair);
    }

    public Node getHead() {
        return head;
    }

    public Node getTail() {
        return tail;
    }

    public int getLength() {
        return length;
    }
    
    @Override
    public String toString() {
        this.printLock.lock();
        try {
            String list = "[";
            Node n = this.head;
            while(n != null) {
                list += Integer.toString(n.getData());
                n = n.getNext();
                if (n != null)
                    list += ", ";
            }
            list += "]";

            return list;
        } finally {
            this.printLock.unlock();
        }
    }
    

    // search and remove
    public void removeValue(int data) {
        this.searchRemoveLock.writeLock().lock();
        this.insertRemoveLock.lock();
        System.out.println("Removing value "+data);
        try {
            if (this.randomDelays)
                Thread.sleep(MIN_DELAY+
                        this.randomGenerator.nextInt(MAX_DELAY-MIN_DELAY));
            
            Node prev = null;
            Node curr = this.head;
            while(curr != null) {
                if (curr.getData() == data) {
                    remove(prev, curr);
                    System.out.println("Value "+data+" removed");
                    System.out.println(this);
                    return;
                }

                prev = curr;
                curr = curr.getNext();
            }
            throw new NoSuchElementException();
        } catch (InterruptedException ex) {
            Logger.getLogger(ConcurrentLinkedList.class.getName()).
                    log(Level.SEVERE, null, ex);
        } catch (NoSuchElementException ex) {
            System.out.println("Value "+data+" not found");
            System.out.println(this);
        } finally {
//            System.out.println(this);
            this.insertRemoveLock.unlock();
            this.searchRemoveLock.writeLock().unlock();
        }
    }
    
    private void remove(Node prev, Node target) {
        if (target == this.head)
            this.head = target.getNext();
        else
            prev.setNext(target.getNext());
        if (target == this.tail)
            this.tail = prev;
        this.length--;
    } 

    // insert
    public void insertValue(int data) {
        this.insertValue(data, false);
    }
    
    // insert
    public void insertValue(int data, boolean hide) {
        this.insertRemoveLock.lock();
        if (!hide)
            System.out.println("Inserting value "+data);
        try {
            Node n = new Node(data);
            if (this.randomDelays)
                Thread.sleep(MIN_DELAY+
                        this.randomGenerator.nextInt(MAX_DELAY-MIN_DELAY));
            if (this.length > 0)
                this.tail.setNext(n);
            else
                this.head = n;

            this.tail = n;
            this.length++;
            if (!hide) {
                System.out.println("Value "+data+
                        " inserted at position "+(this.length-1));
                System.out.println(this);
            }
        } catch(InterruptedException ex) {
            Logger.getLogger(ConcurrentLinkedList.class.getName()).
                    log(Level.SEVERE, null, ex);
        } finally {
//            if (!hide)
//                System.out.println(this);
            this.insertRemoveLock.unlock();  
        }
    }
    
    public int findPosition(int data) {
        this.searchRemoveLock.readLock().lock();
        System.out.println("Searching for value "+data);
        try {
            if (this.randomDelays)
                Thread.sleep(MIN_DELAY+
                        this.randomGenerator.nextInt(MAX_DELAY-MIN_DELAY));
            int pos = 0;
            Node i = this.head;
            while(i != null) {
                if (i.getData() == data) {
                    System.out.println("Value "+data+" found at position "+pos);
                    System.out.println(this);
                    return pos;
                }
                i = i.getNext();
                pos++;
            }
            throw new NoSuchElementException();
        } catch (InterruptedException ex) {
            Logger.getLogger(ConcurrentLinkedList.class.getName()).
                    log(Level.SEVERE, null, ex);
            return -1;
        } catch (NoSuchElementException ex) {
            System.out.println("Value "+data+" not found");
            System.out.println(this);
            return -1;
        } finally {
//            System.out.println(this);
            this.searchRemoveLock.readLock().unlock();
        }
    }
}
