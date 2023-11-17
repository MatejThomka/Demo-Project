import {Line} from "react-chartjs-2";
import {
    LineElement,
    CategoryScale,
    Chart as ChartJS,
    Legend,
    LinearScale,
    Title,
    Tooltip,
    PointElement
} from "chart.js";
import{useEffect, useState} from "react";
import API_URLS_CONFIG from "../../API_URLS_CONFIG";

ChartJS.register(CategoryScale, LinearScale, LineElement, Title, Tooltip, Legend, PointElement);

const ExpenseIncomeLineChart = () => {
    const EXPENSE_API_MONTHLY_INFO_URL = API_URLS_CONFIG.BASE_URL + "/api/expense/each-month"
    const INCOMES_API_MONTHLY_INFO_URL = API_URLS_CONFIG.BASE_URL + "/api/income/each-month"
    const [expenseData, setExpenseData] = useState();
    const [incomeData, setIncomeData] = useState();
    const [loading, setLoading] = useState(true);
    const [notFoundExpenses, setNotFoundExpenses] = useState(false);
    const [notFoundIncomes, setNotFoundIncomes] = useState(false);


    useEffect(() => {
        setLoading(true);
        try {
            fetch(EXPENSE_API_MONTHLY_INFO_URL, {
                method: "GET", headers: {
                    Accept:'application/json', Authorization: 'Bearer ' + localStorage.getItem('token')
                },
            })
                .then((res) => res.json())
                .then((data) => {
                    if (loading) {
                        console.log(data)
                        setExpenseData(data)
                    }
                    if (data.status === "NOT_FOUND") {
                        setNotFoundExpenses(true)
                    } else {
                        setNotFoundExpenses(false)
                    }
                })
        } catch (error) {
            console.log(error);
        }
        try {
            fetch(INCOMES_API_MONTHLY_INFO_URL, {
                method: "GET", headers: {
                    Accept: 'application/json', Authorization: 'Bearer ' + localStorage.getItem('token')
                },
            })
                .then((res) => res.json())
                .then((data) => {
                    if (loading) {
                        console.log(data)
                        setIncomeData(data)
                        setLoading(false)
                    }
                    if (data.status === "NOT_FOUND") {
                        setNotFoundIncomes(true)
                    } else {
                        setNotFoundIncomes(false)
                    }
                })
        } catch (error) {
            console.log(error)
        }
    }, []);

    if (notFoundExpenses === true || notFoundIncomes === true) {
        return (<h2 className="text-center text-red-600 font-semibold text-2xl">There is nothing for now! Add your Incomes & Expenses first!</h2>)
    } else {
        const config = {
            responsive: true, plugin: {
                legend: {position: "chartArea"}, title: {
                    display: true, text: "Modular Line Chart",
                },
            },
            type: 'line',
            data: {expenseData, incomeData},
        }

        const data = {
            labels: expenseData?.map((x) => x?.month),
            datasets: [{
                label: "Expenses",
                data:expenseData?.map((y) => y?.finances),
                fill: true,
                borderColor: 'rgb(210,97,97)',
                backgroundColor: "rgba(10,0,0,0.5)",
                borderWidth: 5,
                borderSkipped: 2,
                minBarLength: 2,
                tension: 0.5
            }, {
                label: "Incomes",
                data: incomeData?.map((y) => y?.finances),
                fill: true,
                borderColor: 'rgb(42,238,12)',
                backgroundColor: "rgba(10,0,0,0.5)",
                borderWidth: 5,
                borderSkipped: 2,
                minBarLength: 2,
                tension: 0.5
            }]
        };


        return (
            <>
                <div className="max-w-[1600px] h-screen flex mx-auto my-auto Line">
                    <div className="border border-gray-400 pt-0 rounded-xl w-full h-fit my-auto shadow-xl">
                        <Line options={config} data={data}/>
                    </div>
                </div>
            </>
        )
    }
}

export default ExpenseIncomeLineChart;