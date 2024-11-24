package classes;

import java.io.Serializable;

public class Sort implements Serializable {

    private static final long serialVersionUID = 1L;
    private int[] vector;
    private int sort;
    public float time;
    static final int MERGESORT = 1;
    static final int HEAPSORT = 2;
    static final int QUICKSORT = 3;
    private int workerID;
    private long finishTime;
    private int[] tempArray;

    public Sort(int[] vector, int sort, long finishTime) {
        this.vector = vector;
        this.sort = sort;
        this.time = 0;
        this.workerID = 0;
        this.finishTime = finishTime;
        this.tempArray = vector;
    }

    public int[] getVector() {
        return vector;
    }

    public int getSort() {
        return sort;
    }

    public float getTime() {
        return this.time;
    }

    public int getWorkerId() {
        return this.workerID;
    }

    public long getFinishTime() {
        return this.finishTime;
    }

    public void sort(int workerId) {
        this.workerID = workerId;
        switch (this.sort) {
            case MERGESORT:
                this.mergeSort(this.vector, 0, vector.length - 1);

            case HEAPSORT:
                this.heapSort(this.vector);

            case QUICKSORT:
                this.quickSort(this.vector, 0, vector.length - 1);
        }
    }

    public void updateArray(){
        this.vector=this.tempArray;
    }
    
    public void mergeSort(int[] array, int left, int right) {
        if (left < right) {//Encuentra el punto medio del vector.
            int middle = (left + right) / 2;

            //Divide la primera y segunda mitad (llamada recursiva).
            mergeSort(array, left, middle);
            mergeSort(array, middle + 1, right);

            //Une las mitades.
            merge(array, left, middle, right);
        }

        this.tempArray = array.clone(); //Copia del vector ordenado
    }

    public void merge(int arr[], int left, int middle, int right) {

        //Encuentra el tamaño de los sub-vectores para unirlos.
        int n1 = middle - left + 1;
        int n2 = right - middle;

        //Vectores temporales.
        int leftArray[] = new int[n1];
        int rightArray[] = new int[n2];

        //Copia los datos a los arrays temporales.
        for (int i = 0; i < n1; i++) {
            leftArray[i] = arr[left + i];
        }
        for (int j = 0; j < n2; j++) {
            rightArray[j] = arr[middle + j + 1];
        }

        //Índices inicial del primer y segundo sub-vector.
        int i = 0, j = 0;

        //Índice inicial del sub-vector arr[].
        int k = left;

        //Ordenamiento
        while (i < n1 && j < n2) {
            if (leftArray[i] <= rightArray[j]) {
                arr[k] = leftArray[i];
                i++;
            } else {
                arr[k] = rightArray[j];
                j++;
            }
            k++;
        }

        //Si quedan elementos por ordenar 
        //Copiar los elementos restantes de leftArray[].
        while (i < n1) {
            arr[k] = leftArray[i];
            i++;
            k++;
        }
        //Copiar los elementos restantes de rightArray[].
        while (j < n2) {
            arr[k] = rightArray[j];
            j++;
            k++;
        }

    }

    public void heapSort(int[] array) {
        int n = array.length;
        // Build heap (rearrange array)
        for (int i = n / 2 - 1; i >= 0; i--) {
            heapify(array, n, i);
        }

        this.tempArray = array.clone(); //Copia del vector ordenado

        // One by one extract an element from heap
        for (int i = n - 1; i > 0; i--) {
            // Move current root to end
            int temp = array[0];
            array[0] = array[i];
            array[i] = temp;
            // Call max heapify on the reduced heap
            heapify(array, i, 0);
        }
    }

    public void heapify(int arr[], int n, int i) {
        int largest = i;
        int l = 2 * i + 1;
        int r = 2 * i + 2;
        // If left child is larger than root
        if (l < n && arr[l] > arr[largest]) {
            largest = l;
        }
        // If right child is larger than largest so far
        if (r < n && arr[r] > arr[largest]) {
            largest = r;
        }
        // If largest is not root
        if (largest != i) {
            int temp = arr[i];
            arr[i] = arr[largest];
            arr[largest] = temp;

            // Recursively heapify the affected sub-tree
            heapify(arr, n, largest);
        }
    }

    public void quickSort(int[] array, int low, int high) {
        if (low < high) {
            int pi = partition(array, low, high);
            quickSort(array, low, pi - 1);
            quickSort(array, pi + 1, high);
        }

        if (low == 0 && high == array.length - 1) {
            this.tempArray = array.clone(); //Copia del vector ordenado 
        }
    }

    public int partition(int[] array, int low, int high) {
        int pivot = array[high];
        int i = low - 1;
        int temp;
        for (int j = low; j < high; j++) {
            if (array[j] <= pivot) {
                i++;
                temp = array[i];
                array[i] = array[j];
                array[j] = temp;
            }
        }
        temp = array[i + 1];
        array[i + 1] = array[high];
        array[high] = temp;
        return i + 1;
    }
}
