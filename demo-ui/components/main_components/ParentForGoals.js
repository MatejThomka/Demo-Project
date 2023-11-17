import { useState } from "react";
import AddGoalButton from "../buttons/AddGoalButton";
import GoalProgressBar from "../charts/GoalProgressBar";
import DeleteGoalButton from "../buttons/DeleteGoalButton";
import DepositGoalButton from "../buttons/DepositGoalButton";
import WithdrawalGoalButton from "../buttons/WithdrawalGoalButton";
import EditGoalButton from "../buttons/EditGoalButton";

const ParentForGoals = () => {
    const [refreshCounter, setRefreshCounter] = useState(0);

    const refresh = () => {
        setRefreshCounter((prevCounter) => prevCounter + 1);
        if (refreshCounter === 10) {
            setRefreshCounter(0)
        }
    };
    console.log(refreshCounter);
return (
        <>
            <h1 className="text-center text-gray-600 font-semibold text-2xl">
                Graphical Summary Of Your Progress With Goal
            </h1>
            <div className="flex place-content-center">
                <div className="flex px-2 items-center">
                    <AddGoalButton refresh={refresh} />
                </div>
                <div className="flex px-2 items-center">
                    <DeleteGoalButton refresh={refresh} />
                </div>
                <div className="flex px-2 items-center">
                    <DepositGoalButton refresh={refresh}/>
                </div>
                <div className="flex px-2 items-center">
                    <WithdrawalGoalButton refresh={refresh} />
                </div>
                <div className="flex px-2 items-center">
                    <EditGoalButton refresh={refresh} />
                </div>
            </div>
            <div>
                <GoalProgressBar key={refreshCounter} />
            </div>

        </>
    );
};

export default ParentForGoals;