package manager;

public class Managers {
    /**
     Виталий, не очень понял ТЗ для этого класса. Сделал как кажется правильным, буду ждать комментариев)
     */
    public static <T extends TaskManager> TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

}
