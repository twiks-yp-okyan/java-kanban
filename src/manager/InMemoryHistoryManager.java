package manager;

import task.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final ArrayList<Task> history = new ArrayList<>();

    private final HashMap<Integer, Node> nodeMap = new HashMap<>();
    private Node head;
    private Node tail;


    @Override
    public void add(Task task) {
        Node nodeForRemove = nodeMap.getOrDefault(task.getId(), null);
        if (nodeForRemove != null) {
            removeNode(nodeForRemove);
        }
        linkLast(task);
        nodeMap.put(task.getId(), this.tail);
    }

    @Override
    public void remove(int id) {
        Node nodeToRemove = nodeMap.getOrDefault(id, null);
        if (nodeToRemove != null) {
            removeNode(nodeToRemove);
        }
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    private void linkLast(Task task) {
        final Node oldTail = this.tail;
        Node newTail = new Node(oldTail, task, null);
        this.tail = newTail;
        if (oldTail == null) {
            this.head = newTail;
        } else {
            oldTail.next = newTail;
        }
    }

    private List<Task> getTasks() {
        List<Task> allTasks = new ArrayList<>();
        Node current = tail;
        while (current != null) {
            allTasks.add(current.item);
            current = current.prev;
        }

        return allTasks;
    }

    private void removeNode(Node node) {
        nodeMap.remove(node.item.getId());

        if (node == head) {
            head = node.next;
            head.prev = null;
        } else if (node == tail) {
            tail = node.prev;
            tail.next = null;
        } else {
            node.prev.next = node.next;
            node.next.prev = node.prev;
        }
    }

    private static class Node {
        Task item;
        Node prev;
        Node next;

        public Node(Node prev, Task item, Node next) {
            this.prev = prev;
            this.item = item;
            this.next = next;
        }
    }
}
