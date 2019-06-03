package com.company.model;

import com.company.model.event.enumeration.ClassType;
import com.company.model.event.enumeration.HyperexpPhaseType;

public class HyperexpSystemState extends SystemState {
    private long N1F1;                  /* CLASS 1 PHASE 1 jobs' number in cloudlet */
    private long N1F2;                  /* CLASS 1 PHASE 2 jobs' number in cloudlet */
    private long N2F1;                  /* CLASS 2 PHASE 1 jobs' number in cloudlet */
    private long N2F2;                  /* CLASS 2 PHASE 2 jobs' number in cloudlet */

    /* init first hyperexp service markov state (0,0,0,0) */
    public HyperexpSystemState() {
        super();
        this.reset();
    }

    public void reset(){
        super.reset();
        this.N1F1 = 0;
        this.N1F2 = 0;
        this.N2F1 = 0;
        this.N2F2 = 0;
    }

    /**
     * --------------------------------------------------------------------------
     * choose the right increment function according to class type and phase type
     * --------------------------------------------------------------------------
     */
    public void incrementNF(ClassType classType, HyperexpPhaseType phaseType) {
        switch (classType) {
            case CLASS1:
                switch (phaseType) {
                    case PHASE_1:
                        this.incrementN1F1();
                        break;
                    case PHASE_2:
                        this.incrementN1F2();
                        break;
                }
                break;
            case CLASS2:
                switch (phaseType) {
                    case PHASE_1:
                        this.incrementN2F1();
                        break;
                    case PHASE_2:
                        this.incrementN2F2();
                        break;
                }
                break;
        }
    }

    /**
     * --------------------------------------------------------------------------
     * choose the right decrement function according to class type and phase type
     * --------------------------------------------------------------------------
     */
    public void decrementNF(ClassType classType, HyperexpPhaseType phaseType) {
        switch (classType) {
            case CLASS1:
                switch (phaseType) {
                    case PHASE_1:
                        this.decrementN1F1();
                        break;
                    case PHASE_2:
                        this.decrementN1F2();
                        break;
                }
                break;
            case CLASS2:
                switch (phaseType) {
                    case PHASE_1:
                        this.decrementN2F1();
                        break;
                    case PHASE_2:
                        this.decrementN2F2();
                        break;
                }
                break;
        }
    }

    public void incrementN1F1() {
        this.N1F1++;
    }

    public void incrementN1F2() {
        this.N1F2++;
    }

    public void incrementN2F1() {
        this.N2F1++;
    }

    public void incrementN2F2() {
        this.N2F2++;
    }

    public void decrementN1F1() {
        this.N1F1--;
    }

    public void decrementN1F2() {
        this.N1F2--;
    }

    public void decrementN2F1() {
        this.N2F1--;
    }

    public void decrementN2F2() {
        this.N2F2--;
    }

    public long getN1F1() {
        return N1F1;
    }

    public long getN1F2() {
        return N1F2;
    }

    public long getN2F1() {
        return N2F1;
    }

    public long getN2F2() {
        return N2F2;
    }
}
