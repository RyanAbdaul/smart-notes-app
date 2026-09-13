import { useState } from "react";
import Buttons from "../common/Buttons";
import { TbPinnedFilled } from "react-icons/tb";
import { TbPinned } from "react-icons/tb";
import { GrAdd } from "react-icons/gr";
import { RiDeleteBin6Line } from "react-icons/ri";
import NoteEditor from "./NoteEditor";
const NoteDetail = () => {
  const [isPinned, setIsPinned] = useState(false);
  const noteDetailNav = [
    {
      value: "Pinned",
      icon: isPinned ? <TbPinnedFilled /> : <TbPinned />,
      onClick: () => setIsPinned((prev) => !prev),
    },
    { value: "Tag", icon: <GrAdd /> },
    { value: "", icon: <RiDeleteBin6Line />,className:"hover:bg-red-100" },
  ];

  return (
    <div className="w-full overflow-auto scrollbar-none [::-webkit-scrollbar]:hidden [-ms-overflow-style:none] h-screen section bg-[#FFFFFF] ">
      <div className="overflow-hidden w-full  flex items-center justify-between px-6 py-3 bg-white border-b border-zinc-200">
        <div className="text-xs text-zinc-500 font-medium">
          Last edited at: 2:00 PM
        </div>

        <div className="flex items-center gap-1 ">
          {noteDetailNav.map((ele, index) => (
            <Buttons
              key={index}
              value={ele.value}
              className={`text-sm p-1.5 rounded-md text-zinc-600 transition-colors  ${ele.className?ele.className:"hover:bg-zinc-100"}`}
              onClick={ele.onClick}
              icon={ele.icon}
            />
          ))}
        </div>
      </div>
      {/* NoteEditor space */}
          <NoteEditor/>
    </div>
  );
};

export default NoteDetail;
