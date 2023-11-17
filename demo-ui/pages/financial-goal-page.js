import Head from 'next/head';
import NavbarOtherPages from "../components/navbars/NavbarOtherPages";
import ParentForGoals from "../components/main_components/ParentForGoals";

export default function Home() {
    return (<div>
        <Head>
            <title>Demo of application</title>
        </Head>
        <NavbarOtherPages/>
        <main>
            <ParentForGoals />
        </main>


    </div>)
}