import Head from 'next/head';
import NavbarOtherPages from "../components/navbars/NavbarOtherPages";
import ExpensesPieChart from "../components/charts/ExpensesPieChart";

export default function Home() {
    return (<div>
        <Head>
            <title>Demo of application</title>
        </Head>
        <NavbarOtherPages/>
        <main>
            <h1 className="text-center text-gray-600 font-semibold text-2xl">Graphical Representation of your monthly expenses.
                </h1>
            <ExpensesPieChart/>
        </main>


    </div>)
}