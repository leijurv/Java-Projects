/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package linkedlist;

import java.util.AbstractList;
import java.util.List;

/**
 *
 * @author leijurv
 */
public class LinkedListNoNull<E> extends AbstractList<E>{
    E current;
    LinkedListNoNull<E> next;
    
    public LinkedListNoNull(){
        current=null;
        next=null;
    }
    
    @Override
    public void clear(){
        current=null;
        if (next==null){
            return;
        }
        next.clear();
    }
    @Override
    public int size(){
        if (current==null){
            if (next==null){
                return 0;
            }
            return next.size();
        }
        if (next==null){
            return 1;
        }
        return 1+next.size();
    }
    public boolean insert(int index, E e){
        if (current==null){
            if (next==null){
                throw new RuntimeException("Out of bounds");
            }
            return next.insert(index,e);
        }
        if (index==1){
            if (current==null){
                current=e;
                return true;
            }
            if (next==null){
                next=new LinkedListNoNull<E>();
                next.current=e;
                return true;
            }
            LinkedListNoNull<E> a=next;
            next=new LinkedListNoNull<E>();
            next.current=e;
            next.next=a;
            return true;
        }
        if (index==0){
            LinkedListNoNull<E> a=next;
            next=new LinkedListNoNull<E>();
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
        if (current==null){
            if (next==null){
                return false;
            }
            return next.contains(obj);
        }
        if (obj.equals(current)){
            return true;
        }
        return next.contains(obj);
    }
    public void add(LinkedListNoNull<E> list){
            if (next==null){
                next=list;
            }else{
                next.add(list);
            }
    }
    @Override
    public boolean add(E e){
        if (next==null){
            if (current==null){
                current=e;
                return true;
            }else{
                next=new LinkedListNoNull<E>();
            }
        }
        return next.add(e);
        
    }
    @Override
    public E get(int index){
        if (current==null){
            if (next==null){
                throw new RuntimeException("Out of bounds");
            }
            return next.get(index);
        }
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
        if (current==null){
            if (next==null){
                throw new RuntimeException("Out of bounds");
            }
            return  next.set(index,e);
        }
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
        if (current==null){
            if (next==null){
                throw new RuntimeException("Out of bounds");
            }
            return next.indexOf(obj);
        }
        if (obj.equals(current)){
            return 0;
        }
        if (next==null){
            throw new RuntimeException("Out of bounds");
        }
        return next.indexOf(obj)+1;
    }
    @Override
    public int lastIndexOf(Object obj){
        if (current==null){
            if (next==null){
                return -1;
            }
            return next.lastIndexOf(obj);
        }
        boolean This=false;
        if (current.equals(obj)){
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
        if (o instanceof LinkedListNoNull){
            LinkedListNoNull<E> l=(LinkedListNoNull<E>)o;
            if (next==null){
                return (current==null&&l.current==null)||(current!=null&&l.current!=null&&current.equals(l.current));
            }
            if (l.current==null){
                return l.next.equals(this);
            }
            if (current==null){
                return next.equals(l);
            }
            if (l.current.equals(current)){
                return l.next.equals(next);
            }
        }
        return false;
    }
    
    @Override
    public E remove(int index){
        if (current==null){
            if (next==null){
                throw new RuntimeException("Out of bounds");
            }
            
            return next.remove(index);
        }
        if (index==0){
            E a=current;
            current=null;
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
    private LinkedListNoNull<E> getList(int index){
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
}
