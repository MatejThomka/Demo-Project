import React, {useState} from "react";
import {useRouter} from "next/router";
import Image from 'next/image';


const FinancialGoalButton = () => {
    const router = useRouter();
    const [isHovering, setIsHovering] = useState(false);
    const onMouseEnter = () => setIsHovering(true);
    const onMouseLeave = () => setIsHovering(false);
    
  const handleClick = async () => {
        await router.push('/financial-goal-page');
    }


    return (<div className="container mx-auto my-8">
        <div className="h-12"
             onMouseEnter={onMouseEnter}
             onMouseLeave={onMouseLeave}>
            <button
                onClick={handleClick}
                className="rounded bg-white text-black px-6 py-2 font-semibold w-[auto]">
                {isHovering ? (<Image className="bg-white"
                                      src="/save_money_transparent.gif"
                                      width={256}
                                      height={256}
                                      alt="Picture of financial goal"/>) : (<Image className="bg-white"
                                                                                   src="/save_money.png"
                                                                                   width={256}
                                                                                   height={256}
                                                                                   alt="Picture of financial goal"/>)}
                Financial Goal
            </button>
        </div>
    </div>)
}

export default FinancialGoalButton;