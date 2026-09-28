import { useState } from "react";
import Buttons from "../common/Buttons";
import { TbPinnedFilled } from "react-icons/tb";
import { TbPinned } from "react-icons/tb";
import { GrAdd } from "react-icons/gr";
import { RiDeleteBin6Line } from "react-icons/ri";
import NoteEditor from "./NoteEditor";
import { useDispatch, useSelector } from "react-redux";
import { deleteNote } from "../../features/notes/notesService";
import {
  showConfirmDelete,
  showSuccessAlert,
  showErrorAlert,
} from "../../utils/sweetAlert";
const NoteDetail = () => {
  const [isPinned, setIsPinned] = useState(false);
  const { activeNoteId, data, isDeleting } = useSelector(
    (state) => state.notes,
  );
  const currentNote = data.find((ele) => ele.id === activeNoteId);
  const dispatch = useDispatch();
  const loading = isDeleting === true;
  const noteDetailNav = [
    {
      value: "Pinned",
      icon: isPinned ? <TbPinnedFilled /> : <TbPinned />,
      onClick: () => setIsPinned((prev) => !prev),
    },
    { value: "Tag", icon: <GrAdd /> },
    {
      value: "",
      icon: <RiDeleteBin6Line />,
      className: "hover:bg-red-100",
      loading: loading,
      onClick: async () => {
        if (!currentNote) return;

        const isConfirmed = await showConfirmDelete();

        if (isConfirmed) {
          try {
            await dispatch(deleteNote(currentNote.id)).unwrap();
            showSuccessAlert("Note deleted successfully!");
          } catch (err) {
            showErrorAlert(err || "Failed to delete note");
          }
        }
      },
    },
  ];
  const formatDate = (date) => {
    const noteDate = new Date(date);
    noteDate.setHours(noteDate.getHours() + 3);

    const now = new Date();
    const isToday = noteDate.toDateString() === now.toDateString();

    if (isToday) {
      return (
        <div className="text-xs text-zinc-500 font-medium">
          last update :Today at
          {noteDate.toLocaleString("en-US", {
            hour: "2-digit",
            minute: "2-digit",
          })}
        </div>
      );
    }
    const yesterday = new Date();
    yesterday.setDate(yesterday.getDate() - 1);
    const isYesterday = noteDate.toDateString() === yesterday.toDateString();
    if (isYesterday) {
      return <div>Yesterday</div>;
    }
    return noteDate.toLocaleDateString("en-US", {
      month: "short",
      day: "numeric",
      year: "numeric",
    });
  };

  return (
    <div className="w-full overflow-auto scrollbar-none [::-webkit-scrollbar]:hidden [-ms-overflow-style:none] h-screen section bg-[#FFFFFF] ">
      <div className="overflow-hidden w-full  flex items-center justify-between px-6 py-3 bg-white border-b border-zinc-200">
        <div className="text-xs text-zinc-500 font-medium">
          {currentNote ? formatDate(currentNote.updatedAt) : "Last update: Now"}
        </div>

        <div className="flex items-center gap-1 ">
          {noteDetailNav.map((ele, index) => (
            <Buttons
              loading={ele.loading}
              key={index}
              value={ele.value}
              className={`text-sm p-1.5 rounded-md text-zinc-600 transition-colors  ${ele.className ? ele.className : "hover:bg-zinc-100"}`}
              onClick={ele.onClick}
              icon={ele.icon}
            />
          ))}
        </div>
      </div>
      {/* NoteEditor space */}
      <NoteEditor />
    </div>
  );
};

export default NoteDetail;
