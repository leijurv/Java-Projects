/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package linkedlist;

import java.util.*;
/**
 *
 * @author leif
 */
public class LinkedList<E> extends AbstractList<E>{
    E current;
    LinkedList<E> next;
    
    public LinkedList(){
        current=null;
        next=null;
    }
    
    @Override
    public int size(){
        if (next==null){
            return 1;
        }
        return 1+next.size();
    }
    public boolean insert(int index, E e){
        if (index==1){
            if (next==null){
                next=new LinkedList<E>();
                next.current=e;
                return true;
            }
            LinkedList<E> a=next;
            next=new LinkedList<E>();
            next.current=e;
            next.next=a;
            return true;
        }
        if (index==0){
            LinkedList<E> a=next;
            next=new LinkedList<E>();
            next.current=current;
            next.next=a;
            current=e;
            return true;
        }
        if (next==null){
            throw new RuntimeException("Out of bounds");
        }
        return next.insert(index-1,e);
    }
    @Override
    public boolean isEmpty(){
        return size()==0;
    }
    @Override
    public boolean contains(Object obj){
        if (obj==null){
            if (next==null){
                return current==null;
            }
            return current==null || next.contains(obj);
        }
        if (obj.equals(current)){
            return true;
        }
        return next.contains(obj);
    }
    public void add(LinkedList<E> list){
            if (next==null){
                next=list;
            }else{
                next.add(list);
            }
    }
    @Override
    public boolean add(E e){
        if (next==null){
            
            next=new LinkedList();
            next.current=e;
            return true;
        }
        return next.add(e);
        
    }
    @Override
    public E get(int index){
        if (index==0){
            return current;
        }
        if (next==null){
                throw new RuntimeException("Out of bounds");
        }
        return next.get(index-1);
    }
    @Override
    public E set(int index,E e){
        if (index==0){
            E a=current;
            current=null;
            current=e;
            return a;
        }
        if (next==null){
                throw new RuntimeException("Out of bounds");
        }
        return next.set(index-1,e);
    }
    @Override
    public int indexOf(Object obj){
        if (obj==null){
            if (next==null){
                return (current==null)?0:-1;
            }
            return (next.indexOf(obj))==-1?(current==null?0:-1):-1;
        }
        if (obj.equals(current)){
            return 0;
        }
        if (next==null){
            return -1;
        }
        int i=next.indexOf(obj);
        return i==-1?i:i+1;
    }
    @Override
    public int lastIndexOf(Object obj){
        boolean This=false;
        if (obj==null?current==null:current.equals(obj)){
            if (next==null){
                return 0;
            }
            This=true;
        }
        if (next==null){
            return This?0:-1;
        }
        int Next=next.lastIndexOf(obj);
            if (Next==-1){
                return This?0:-1;
            }else{
                return 1+Next;
            }
    }
    @Override
    public boolean equals(Object o){
        if (o instanceof LinkedList){
            LinkedList<E> l=(LinkedList<E>)o;
            if (next==null){
                return (current==null&&l.current==null)||(current!=null&&l.current!=null&&current.equals(l.current));
            }
            if (l.current==null){
                return current==null;
            }
            if (current==null){
                return l.current==null;
            }
            if (l.current.equals(current)){
                return l.next.equals(next);
            }
        }
        return false;
    }
    
    @Override
    public E remove(int index){
        if (index==0){
            E a=current;
            current=null;
            current=next.current;
            next=next.next;
            return a;
        }
        if (next==null){
            throw new RuntimeException("Out of bounds");
        }
        return  next.remove(index-1);
    }
    @Override
    public int hashCode(){
        if (next==null){
            return 1;
        }
        return (31*next.hashCode())+(current==null?0:current.hashCode());
    }
    @Override
    public void add(int i, E e) {
        insert(i,e);
    }
    private LinkedList<E> getList(int index){
        if (next==null){
                throw new RuntimeException("Out of bounds");
        }
        if (index==0){
            return this;
        }
        return next.getList(index-1);
   }
    @Override
public final List<E> subList(int a, int b){
    if (b==size()){
        return getList(a);
    }
    return super.subList(a,b);
}
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        LinkedList<Integer> ll=new LinkedList<Integer>();
        ll.add(5);
        ll.add(0);
        ll.add(2);
        ll.add(1);
        ll.subList(2,4).clear();
        for (int i=0; i<ll.size(); i++){
            System.out.println(ll.get(i));
        }
    }
}