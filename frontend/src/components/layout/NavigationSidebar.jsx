import  {  useState } from "react";
import {  useSelector } from "react-redux";
import { FaNotesMedical } from "react-icons/fa6";
import { TbPinned } from "react-icons/tb";
import { IoMdTime } from "react-icons/io";
import { IoTrashOutline } from "react-icons/io5";
import Buttons from "../common/Buttons";
import { FaArrowDown } from "react-icons/fa";
import { LuNotebook } from "react-icons/lu";
import { FaArrowUp } from "react-icons/fa";
const naviElements = [
  { value: "All Notes", icon: <FaNotesMedical /> },
  { value: "Pinned & Favorates", icon: <TbPinned /> },
  { value: "Recent", icon: <IoMdTime /> },
  { value: "Trash", icon: <IoTrashOutline /> },
];
const naviNoteBook = ["Backend Notes", "Architecture & API", "Personal Ideas"];
const naviTags = ["spring-boot", "react", "git-workflow"];
const NavigationSidebar = () => {
  const { data } = useSelector((state) => state.notes);


  const [isNoteBookOpen, setIsNoteBookOpen] = useState(true);
  const [isTagOpen, setIsTagOpen] = useState(true);

  return (
    <div className=" h-screen overflow-auto scrollbar-none [::-webkit-scrollbar]:hidden [-ms-overflow-style:none] ">
      {/* AllNotes */}
      {naviElements.map((ele, index) => (
        <Buttons
          key={index}
          value={ele.value}
          count={ele.value=="All Notes"?data?.length:"0"}
          icon={ele.icon}
        />
      ))}
      {/* Notebook */}
      <div className="p-2">
        <Buttons onClick={()=>setIsNoteBookOpen(!isNoteBookOpen)} value={"Notebook"} icon={isNoteBookOpen ? <FaArrowDown /> :<FaArrowUp />} />
      </div>
      {isNoteBookOpen&&naviNoteBook.map((ele, index) => (
        <Buttons key={index} value={ele} count={0} icon={<LuNotebook />} />
      ))}
      {/* Tags  */}
      <div className="p-2">
        <Buttons onClick={()=>setIsTagOpen(!isTagOpen)} value={"TAGS"} icon={isTagOpen ? <FaArrowDown />:<FaArrowUp />} />
      </div>
      {isTagOpen&&naviTags.map((ele, index) => (
        <Buttons key={index} value={ele} count={0} icon="#" />
      ))}
    </div>
  );
};

export default NavigationSidebar;
