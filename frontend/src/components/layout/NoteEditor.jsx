import { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import CheckBox from "../common/CheckBox";
import { MdDataSaverOn } from "react-icons/md";
import { FaRegEdit } from "react-icons/fa";
import { GiConfirmed } from "react-icons/gi";
import { createNote, updateNote } from "../../features/notes/notesService";
import { setMode } from "../../features/notes/notesSlice";
import ErrorHandler from "../../utils/ErrorHandler";
import Spinner from "../../utils/Spinner";
import { BiSolidBookmarks } from "react-icons/bi";

const NoteEditor = () => {
  const { data, activeNoteId, mode, error, isLoading } = useSelector(
    (state) => state.notes,
  );
  const [noteError, setNoteError] = useState(null);
  const loadingStatus = isLoading === true;
  const [formData, setFormData] = useState({ title: "", description: "" });
  const currentNote = data.find((ele) => ele.id === activeNoteId);

  const [isOpen, setIsOpen] = useState(false);
  const [message, setMessage] = useState(null);

  useEffect(() => {
    if (currentNote) {
      setFormData({
        title: currentNote.title,
        description: currentNote.description,
      });
    } else {
      setFormData({
        title: "",
        description: "",
      });
    }
  }, [activeNoteId]);
  useEffect(() => {
    if (error) {
      setNoteError(error);
    }
  }, [error]);
  const dispatch = useDispatch();

  const handleSubmit = (e) => {
    e.preventDefault();
  };

  const handleCreate = () => {
    dispatch(setMode("create"));
    setFormData({
      title: "",
      description: "",
    });
    setNoteError(null);
    setMessage(null);
    // استخدام prev لضمان فتح/إغلاق القائمة بشكل صحيح ومتسق
    setIsOpen((prev) => !prev);
  };

  const handleUpdate = () => {
    dispatch(setMode("edit"));
    setNoteError(null);
    setMessage(null);
    setIsOpen((prev) => !prev);
  };

  const handleNote = async () => {
    if (mode === "view") {
      // إذا كانت في وضع العرض وضغط الزر، افتح أو اغلق قائمة الخيارات بدلاً من حفظ فارغ
      setIsOpen((prev) => !prev);
      return;
    }

    if (mode === "create") {
      if (!formData.title.trim()) {
        setNoteError("At Least Fill Title");
        return;
      }
      try {
        // الانتظار حتى يكتمل الطلب بنجاح تام من السيرفر
        await dispatch(createNote(formData)).unwrap();
        dispatch(setMode("view"));
        setMessage("Saved");
        setIsOpen(false);
      } catch (err) {
        // إذا حدث خطأ، سيتوقف هنا ولن تظهر رسالة النجاح
        setNoteError(err || "Failed to create note");
      }
    } else if (mode === "edit" && currentNote) {
      if (!formData.title.trim()) {
        setNoteError("At Least Fill Title");
        return;
      }
      if (
        currentNote.title.trim() === formData.title.trim() &&
        currentNote.description.trim() === formData.description.trim()
      ) {
        setNoteError("There are no Changes");
        return;
      }
      try {
        await dispatch(
          updateNote({
            id: currentNote.id,
            title: formData.title,
            description: formData.description,
          }),
        ).unwrap();
        dispatch(setMode("view"));
        setMessage("Changes have saved");
        setIsOpen(false);
      } catch (err) {
        setNoteError(err || "Failed to update note");
      }
    }
  };

  const handleChange = (e) => {
    if (mode === "view") {
      setNoteError("Select Option");
      setMessage(null);

      return;
    }
    setNoteError(null);
    setMessage(null);

    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  return (
    <section className="p-2 h-full relative">
      {noteError && <ErrorHandler error={noteError} />}
      {message && (
        <div className="p-2 flex items-center justify-center gap-1 bg-green-500 rounded-2xl text-white font-bold text-center mb-3">
          <span className="text-xl">
            <BiSolidBookmarks />
          </span>
          {message}
        </div>
      )}
      <form onSubmit={handleSubmit}>
        <textarea
          style={{ fieldSizing: "content" }}
          className="outline-hidden resize-none overflow-hidden w-full h-full text-3xl font-bold mb-3"
          value={formData?.title}
          name="title"
          dir="auto"
          onChange={handleChange}
          placeholder="Heading"
          required
        />
        <textarea
          style={{ fieldSizing: "content" }}
          className="outline-hidden resize-none overflow-hidden w-full h-full text-lg"
          name="description"
          dir="auto"
          value={formData?.description}
          onChange={handleChange}
          placeholder="Details"
          required
        />
        <CheckBox />

        {/* زر الإجراء الأساسي (حفظ أو فتح القائمة حسب الـ mode) */}
        <button
          type="button"
          onClick={handleNote}
          disabled={loadingStatus}
          className="fixed bottom-20 right-5 mr-2 scale-140 hover:cursor-pointer text-amber-600 text-4xl hover:scale-150 hover:bg-amber-600 hover:text-amber-50 rounded-2xl transition-all z-10"
        >
          {loadingStatus ? (
            <Spinner />
          ) : mode === "view" ? (
            <MdDataSaverOn />
          ) : (
            <GiConfirmed />
          )}
        </button>

        {/* Create button */}
        <button
          type="button"
          onClick={handleCreate}
          className={`fixed bottom-30 right-22 mr-2 scale-140 text-amber-600 text-4xl hover:cursor-pointer hover:scale-150 hover:bg-amber-600 hover:text-amber-50 rounded-2xl transition-all duration-300 ease-out z-10 ${
            isOpen
              ? "translate-x-0 translate-y-0 opacity-100"
              : "translate-x-17 translate-y-10 opacity-0 pointer-events-none"
          }`}
        >
          <MdDataSaverOn />
        </button>

        {/* Update button */}
        {currentNote && (
          <button
            type="button"
            disabled={!currentNote || loadingStatus}
            onClick={handleUpdate}
            style={{ cursor: !currentNote ? "not-allowed" : "pointer" }}
            className={`fixed bottom-10 right-22 mr-2 p-1.5 scale-140 text-amber-600 text-2xl hover:cursor-pointer hover:scale-150 hover:bg-amber-600 hover:text-amber-50 rounded-2xl transition-all duration-300 ease-out z-10 ${
              isOpen
                ? "translate-x-0 translate-y-0 opacity-100"
                : "translate-x-27 translate-y-10 opacity-0 pointer-events-none"
            }`}
          >
            <FaRegEdit />
          </button>
        )}
      </form>
    </section>
  );
};

export default NoteEditor;
