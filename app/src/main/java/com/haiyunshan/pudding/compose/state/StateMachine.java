package com.haiyunshan.pudding.compose.state;

import com.haiyunshan.pudding.compose.ComposeFragment;

import java.util.ArrayList;

public class StateMachine {

    public static final String READ     = "read";
    public static final String EDIT     = "edit";
    public static final String FORMAT   = "format";

    ArrayList<BaseState> mList;

    ArrayList<BaseState> mStates;

    ComposeFragment mParent;

    public StateMachine(ComposeFragment parent) {
        this.mParent = parent;

        this.mList = new ArrayList<>();
        this.mStates = new ArrayList<>();
    }

    public BaseState peek() {
        if (mList.isEmpty()) {
            return null;
        }

        return mList.get(mList.size() - 1);
    }

    public BaseState pop() {
        if (mList.size() <= 1) {
            return null;
        }

        BaseState state = mList.remove(mList.size() - 1);
        state.onExit();

        BaseState current = mList.get(mList.size() - 1);
        current.onEnter();

        mStates.add(state);
        return state;
    }

    public BaseState push(String stateId) {
        boolean isEmpty = mList.isEmpty();

        if (!isEmpty) {
            int index = mList.size() - 1;
            BaseState state = mList.get(index);
            if (state.mId.equals(stateId)) {
                return state;
            }
        }

        BaseState state = this.obtain(stateId);
        mList.add(state);

        if (!isEmpty) {
            state = mList.get(mList.size() - 2);
            state.onExit();

            BaseState current = mList.get(mList.size() - 1);
            current.onEnter();
        }

        return state;
    }

    BaseState obtain(String stateId) {
        BaseState target = null;
        for (BaseState state : mStates) {
            if (state.mId.equalsIgnoreCase(stateId)) {
                target = state;
                break;
            }
        }

        if (target != null) {
            mStates.remove(target);
            return target;
        }

        target = createState(stateId);
        return target;
    }

    BaseState createState(String stateId) {
        BaseState state = null;
        if (stateId.equalsIgnoreCase(READ)) {
            state = new ReadState(mParent);
        } else if (stateId.equalsIgnoreCase(EDIT)) {
            state = new EditState(mParent);
        } else if (stateId.equalsIgnoreCase(FORMAT)) {
            state = new FormatState(mParent);
        }

        return state;
    }
}
