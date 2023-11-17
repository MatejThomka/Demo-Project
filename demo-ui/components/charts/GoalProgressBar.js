import {Bar} from "react-chartjs-2";
import {BarElement, CategoryScale, Chart as ChartJS, Legend, LinearScale, Title, Tooltip} from "chart.js";
import {useEffect, useState, useRef} from "react";
import API_URLS_CONFIG from "../../API_URLS_CONFIG";

ChartJS.register(CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend);

const GoalProgressBar = () => {
    const GOAL_GET_ALL_URL = API_URLS_CONFIG.BASE_URL + "/api/goal/goals"
    const EXPENSE_COUNT_ALL_URL = API_URLS_CONFIG.BASE_URL + "/api/expense/countAll"
    const [goalData, setGoalData] = useState();
    const [expenseData, setExpenseData] = useState();
    const [loading, setLoading] = useState(true);
    const [notFoundGoals, setNotFoundGoals] = useState(false);
    const [notFoundExpenses, setNotFoundExpenses] = useState(false);
    const isMounted = useRef(true);

    useEffect(() => {
        loadData();

        return () => {
            isMounted.current = false;
        };
    }, []);
    const loadData = async () => {
        setLoading(true);
        try {
            await fetch(GOAL_GET_ALL_URL, {
                method: "GET", headers: {
                    Accept: 'application/json', Authorization: 'Bearer ' + localStorage.getItem('token')
                },
            })
                .then((res) => res.json())
                .then((data) => {
                    if (isMounted.current) {
                        if (loading) {
                            setGoalData(data);
                        }
                        if (data.status === "NOT_FOUND") {
                            setNotFoundGoals(true)
                        } else {
                            setNotFoundGoals(false)
                        }
                    }
                })
                .catch((error) => {
                    console.log("Goal Data Error:", error);
                });
        } catch (error) {
            console.log("Goal Data Error:", error)
        }
        try {
            await fetch(EXPENSE_COUNT_ALL_URL, {
                method: "GET", headers: {
                    Accept: 'application/json', Authorization: 'Bearer ' + localStorage.getItem('token')
                },
            })
                .then((res) => res.json())
                .then((data) => {
                    if (isMounted.current) {
                        if (loading) {
                            setExpenseData(data);
                            setLoading(false);
                        }
                        if (data.status === "NOT_FOUND") {
                            setNotFoundExpenses(true)
                        } else {
                            setNotFoundExpenses(false)
                        }
                    }
                })
                .catch((error) => {
                    console.log("Expense Data Error:", error);
                });
        } catch (error) {
            console.log("Expense Data Error:", error)
        }
    };

    if (notFoundGoals === true || notFoundExpenses === true) {
        return (
            <h2 className="text-center text-red-600 font-semibold text-2xl">There is nothing for now! Create some Goal
                first!</h2>)
    } else {
        const config = {
            responsive: true, plugin: {
                legend: {position: "chartArea"}, title: {
                    display: true, text: "Modular Bar Chart",
                },
            }, data: {goalData, expenseData}
        };

        const data = {
            labels: goalData?.map((x) => x.name), datasets: [{
                label: "Spending",
                data: goalData?.map(() => expenseData?.expense),
                borderColor: "rgb(204,75,75)",
                backgroundColor: "rgba(178,58,58,0.5)",
                borderWidth: 2,
                borderSkipped: 2,
                minBarLength: 10,
                borderRadius: 10,
            }, {
                label: "Current Balance",
                data: goalData?.map((y) => y.currentBalance),
                borderColor: "rgb(34,229,13)",
                backgroundColor: "rgba(45,222,26,0.5)",
                borderWidth: 2,
                borderSkipped: 2,
                minBarLength: 10,
                borderRadius: 10,
            }, {
                label: "Target of Goal",
                data: goalData?.map((y) => y.targetGoal),
                borderColor: "rgb(9,166,239)",
                backgroundColor: "rgba(30,187,211,0.5)",
                borderWidth: 2,
                borderSkipped: 2,
                minBarLength: 10,
                borderRadius: 10,
            }],
        };

        return (<>
            <div className="max-w-[1600px] h-screen flex mx-auto my-auto Bar">
                <div className='border border-gray-400 pt-0 rounded-xl w-full h-fit my-auto shadow-xl'>
                    <Bar options={config} data={data}/>
                </div>
            </div>
        </>);
    }

}

export default GoalProgressBar;