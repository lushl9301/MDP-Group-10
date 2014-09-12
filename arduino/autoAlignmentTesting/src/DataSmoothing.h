#ifndef DataSmoothing_h
#define DataSmoothing_h

class DataSmoothing {
public:
    DataSmoothing();
    ~DataSmoothing();
    
    void windowFilter7(int *ptr2RawData, int head);
};
#endif

